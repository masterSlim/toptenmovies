package com.mrslim.toptenmovies.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mrslim.toptenmovies.config.ParserConfig;
import com.mrslim.toptenmovies.entities.MovieEntity;
import com.mrslim.toptenmovies.repositories.MovieRepository;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* Так как сайт kinopoisk.ru регулярно меняет URI адреса и имена в html разметке для защиты от парсинга,
 * то эти переменные по максимуму вынесены из классов в конфигурационные файлы, где их можно оперативно
 * править все в одном месте, не выискивая в коде.
 * Парсер может работать в двух режимах - онлайн и оффлайн. В онлайн режиме данные берутся с сайта kinopoisk.ru,
 * в оффлайн режиме - с локальной копии страницы. Переключить режим можно в конфигурационном файле.
 * В данном парсере в онлайн режиме к GET-запросу добавляются фейковые заголовки, взятые из моего браузера,
 * чтобы замаскировать запрос под пользовательский, а не автоматический и постараться избежать редиректа на капчу.
 * Считаю эту минимальную настройку, а так же возможность тестово запустить парсер оффлайн, достаточной для целей
 * тестового задания, так как написание более продвинутого и "хитрого" парсера займёт больше времени и будет требовать
 * постоянной модификации по мере развития и изменения защиты Кинопоиска от парсеров.
 *
 * В будущем, при развитии этого проекта можно подключить строковый браузер и сделать несколько методов, сменяющих друг
 * друга при провале одного из них.
 * */

@Service
public class ParseMovieService implements MovieService {
    private final String site = "https://www.kinopoisk.ru";
    @Autowired
    private ParserConfig parserConfig;
    @Autowired
    MovieRepository movieRepository;

    @Override
    public LinkedList<MovieEntity> getMovies(int forDate, int amount) throws IOException {
        LinkedList<MovieEntity> result;
        if (parserConfig.isOnline()) {
            String getTopTemplate = parserConfig.getGetTopTemplate().replace("DATE", Integer.toString(forDate));
            Connection con = Jsoup.connect(site + getTopTemplate).headers(getHeaders());
            Document page = con.get();
            result = new LinkedList<>(parse(page).subList(0, amount));
        } else {
            Document page = Jsoup.parse(new File(parserConfig.getTestFile()), "UTF-8");
            result = new LinkedList<>(parse(page).subList(0, amount));
        }

        return result;
    }

    //TODO: добавить выбор метод с выбором диапазона годов
    public LinkedList<MovieEntity> getMovies(int forDate) throws URISyntaxException, IOException, InterruptedException {
        return getMovies(forDate, 10);
    }

    /**
     * Метод для парсинга страницы и поиска информации о фильмах. Парсинг основывается на нахождении блока-маркера,
     * с названием фильма, далее из родительского блока извлекается остальная информация.
     * Назваение css-классов блока-маркера и остальных блоков информации указывается вручную в конфигурационном файле.
     *
     * @param page Объект, представляющий собой страницу, в которой будет производится поиск информации о фильмах.
     * @return Список объектов MovieEntity созданных на основе данных из page.
     */

    private LinkedList<MovieEntity> parse(Document page) {
        LinkedList<MovieEntity> result = new LinkedList<>();
        double rating;
        long votes;
        int year;
        String originalName;
        String ratingCssClass = parserConfig.getRatingCssClass();
        String titleClass = parserConfig.getTitleCssClass();
        String secondaryTextCssClass = parserConfig.getSecondaryTextCssClass();
        String votesCssClass = parserConfig.getVotesCssClass();

        Elements titleEl = page.getElementsByClass(titleClass);
        for (int i = 0; i < titleEl.size(); i++) {
            Element title = titleEl.get(i);
            Element parent = title.parents().get(5);
            rating = Double.parseDouble(parent.getElementsByClass(ratingCssClass).text());
            votes = Long.parseLong(parent.getElementsByClass(votesCssClass).text().replace(" ", ""));

            /* Год, оригинальное название и длительность фильма на сайте собраны в одну строку и указаны через запятую.
             * Далее в методе обрабатывается ряд ограничений при парсинге оригинального названия:
             * • Оно указывается не всегда, например для российских фильмов оно отсутствует;
             * • Может содержать запятые;
             * • Может содержать год в названии (например фильм "1917");
             * • Указано в начале строки;
             * Моим решением было взять за основу 2 записи, которые присуствуют в строке всегда - год и длительность,
             * разделенные одной (последней в строке) запятой и пробелом и обрабатывать строку в обратном направлении.
             * Если более в строке ничего не указано, то индекс второй запятой, отсчитываемый в обратном направлении
             * от индекса последней, будет равен -1. В этом случае строка делится на 2 части - год и длительность,
             * и за результат берется первая часть, а поле originalName заполняется из титульного элемента.
             * Если же в строке будет найдена как минимум ещё одна запятая, значит строка состоит из 3 частей:
             * оригинального названия,года и длительности, из которых обрабатываются название и год.
             * */

            String secondaryText = parent.getElementsByClass(secondaryTextCssClass).text();
            int b = secondaryText.lastIndexOf(", ");
            int a = secondaryText.lastIndexOf(", ", b - 1);
            if (a != -1) {
                originalName = secondaryText.substring(0, a);
                Pattern p = Pattern.compile("\\d{4}");
                Matcher m = p.matcher(secondaryText.substring(a, b));
                m.find();
                year = Integer.parseInt(m.group());
            } else {
                String[] secondaryData = secondaryText.split(", ");
                year = Integer.parseInt(secondaryData[0]);
                originalName = parent.getElementsByClass(titleClass).text();
            }
            MovieEntity movie = new MovieEntity(rating, originalName, year, votes);
            result.add(movie);
        }
        return result;
    }

    /**
     * Метод для чтения фейковых заголовков из файла, указанного в конфигурации application.yaml
     *
     * @return Возвращает карту с парами "Имя заголовка" - "Значение заголовка"
     * @throws IOException Выбрасывает исключение, если не удаётся открыть файл
     **/

    private Map<String, String> getHeaders() throws IOException {
        Map<String, String> headers = new LinkedHashMap<>();
        File headersFile = new File(parserConfig.getHeadersFile());
        ObjectMapper mapper = new ObjectMapper();
        List<HashMap<String, String>> list = mapper.readValue(headersFile,
                new TypeReference<LinkedList<HashMap<String, String>>>() {
                });
        for (HashMap<String, String> hm : list) {
            headers.put(hm.get("name"), hm.get("value"));
        }
        return headers;
    }
}
