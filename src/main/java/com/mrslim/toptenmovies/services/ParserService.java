package com.mrslim.toptenmovies.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mrslim.toptenmovies.config.GeneralConfig;
import com.mrslim.toptenmovies.config.Mode;
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
public class ParserService implements MovieService {
    private final String site = "https://www.kinopoisk.ru";
    @Autowired
    private GeneralConfig appConfig;
    @Autowired
    private ParserConfig parserConfig;
    @Autowired
    private MovieRepository movieRepo;

    @Override
    public LinkedList<MovieEntity> getMovies(int... years) throws IOException {
        LinkedList<MovieEntity> result;
        Document page = getPage(years);
        result = parse(page);
        if (result.size() <= appConfig.getSize()) return result;
        else result = new LinkedList<>(result.subList(0, appConfig.getSize()));
        return result;
    }

    private Document getPage(int...years) throws IOException {
        Document page;
        String date = (years.length == 2) ? years[0] + "-" + years[1] : Integer.toString(years[0]);
        if (appConfig.getMode() == Mode.ONLINE) {
            String getChartTemplate = parserConfig.getChartTemplate().replace("DATE", date);
            Connection con = Jsoup.connect(site + getChartTemplate).headers(getHeaders());
            page = con.get();
        } else {
            File dir = new File(parserConfig.getOfflineDir());
            if (!dir.isDirectory()) throw new IOException("В конфигурационном файле неверно указана офлайн директория");
            String offlineFile = date + ".htm";
            File file = null;
            File[] files = dir.listFiles();
            if (files == null || (files.length == 0)) throw new IOException("В офлайн директории нет файлов");
            for (File f : files) {
                if (f.getName().equals(offlineFile)) file = f;
            }
            if (file == null) return null;
            page = Jsoup.parse(file, "UTF-8");
        }
        return page;
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
        int[] year;
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

            // Если присутствует оригинальное название
            if (a != -1) {
                originalName = secondaryText.substring(0, a);
                Pattern p = Pattern.compile("\\d{4}"); // d{4} четыре цифры
                Matcher m = p.matcher(secondaryText.substring(a, b));
                m.find();
                int fromYear = Integer.parseInt(m.group());
                int toYear;
                if (m.find()) {
                    toYear = Integer.parseInt(m.group());
                    year = new int[]{fromYear, toYear};
                } else year = new int[]{fromYear};
            }

            // Если присутствует только год и длительность
            else {
                String[] secondaryData = secondaryText.split(", ");
                Pattern p = Pattern.compile("\\d{4}"); // d{4} четыре цифры
                Matcher m = p.matcher(secondaryData[0]);
                m.find();
                int fromYear = Integer.parseInt(m.group());
                int toYear;
                if (m.find()) {
                    toYear = Integer.parseInt(m.group());
                    year = new int[]{fromYear, toYear};
                } else year = new int[]{fromYear};
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
