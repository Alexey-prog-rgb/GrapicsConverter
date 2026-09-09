package ru.netology.graphics.image;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

public class TextGrapicsConverterRelease implements TextGraphicsConverter {
    private final int DEFAULT_NO_PARAM = -1;

    private int maxWidth;
    private int maxHeight;
    private double maxRatio;
    private TextColorSchema textColorSchema;

    public TextGrapicsConverterRelease() {
        this.maxWidth = DEFAULT_NO_PARAM;
        this.maxHeight = DEFAULT_NO_PARAM;
        this.maxRatio = DEFAULT_NO_PARAM;
        this.textColorSchema = new TestColorSchemaRelease();
    }

    @Override
    public void setMaxWidth(int maxWidth) {
        if (maxWidth <= 0) {
            throw new IllegalArgumentException("Ширина изображения должна быть положительным числом, передано: " + maxWidth);
        }
        this.maxWidth = maxWidth;
    }

    @Override
    public void setMaxHeight(int maxHeight) {
        if (maxHeight <= 0) {
            throw new IllegalArgumentException("Высота изображения должна быть положительным числом, передано: " + maxHeight);
        }
        this.maxHeight = maxHeight;
    }

    @Override
    public void setMaxRatio(double maxRatio) {
        if (maxRatio <= 0) {
            throw new IllegalArgumentException("Соотношение сторон изображения должно быть положительным числом, передано: " + maxRatio);
        }
        this.maxRatio = maxRatio;
    }

    @Override
    public void setTextColorSchema(TextColorSchema textColorSchema) {
        if (textColorSchema == null) {
            throw new IllegalArgumentException("Цветовая схема не может быть null");
        }
        this.textColorSchema = textColorSchema;
    }

    @Override
    public String convert(String url) throws IOException, BadImageSizeException {
        // Вот так просто мы скачаем картинку из интернета :)
        BufferedImage img = ImageIO.read(new URL(url));
        // отладка - вывод скаченного изображения
        //ImageIO.write(img, "png", new File("1 - RGB.png"));

        // Если конвертер попросили проверять на максимально допустимое
        // соотношение сторон изображения, то вам здесь нужно сделать эту проверку,
        // и, если картинка не подходит, выбросить исключение BadImageSizeException.
        // Чтобы получить ширину картинки, вызовите img.getWidth(), высоту - img.getHeight()

        // Если конвертеру выставили максимально допустимые ширину и/или высоту,
        // вам нужно по ним и по текущим высоте и ширине вычислить новые высоту
        // и ширину.
        // Соблюдение пропорций означает, что вы должны уменьшать ширину и высоту
        // в одинаковое количество раз.
        // Пример 1: макс. допустимые 100x100, а картинка 500x200. Новый размер
        // будет 100x40 (в 5 раз меньше).
        // Пример 2: макс. допустимые 100x30, а картинка 150x15. Новый размер
        // будет 100x10 (в 1.5 раза меньше).
        // Подумайте, какими действиями можно вычислить новые размеры.
        // Не получается? Спросите вашего руководителя по курсовой, поможем.

        int imgWidth = img.getWidth();
        int imgHeight = img.getHeight();
        double imgRatio = (double) imgWidth / (double) imgHeight;

        int newWidth = imgWidth;
        int newHeight = imgHeight;

        // Установлено ограничение на ширину
        if (maxWidth != DEFAULT_NO_PARAM && newWidth > maxWidth) {
            newWidth = maxWidth;
        }

        // Установлено ограничение на высоту
        if (maxHeight != DEFAULT_NO_PARAM && newHeight > maxHeight) {
            newHeight = maxHeight;
        }

        // Установлено ограничение на соотношение ширины к высоте
        if (maxRatio != DEFAULT_NO_PARAM && imgRatio > maxRatio) {
            imgRatio = maxRatio;
            newHeight = (int) (newWidth / imgRatio);
        }

        // Теперь нам нужно попросить картинку изменить свои размеры на новые.
        // Последний параметр означает, что мы просим картинку плавно сузиться
        // на новые размеры. В результате мы получаем ссылку на новую картинку, которая
        // представляет собой суженную старую.
        Image scaledImage = img.getScaledInstance(newWidth, newHeight, BufferedImage.SCALE_SMOOTH);

        // отладка - вывод перемасштабированной картинки
//        BufferedImage bi = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_3BYTE_BGR);
//        Graphics2D g2d = bi.createGraphics();
//        g2d.drawImage(scaledImage, 0, 0,null);
//        ImageIO.write(bi, "png", new File("2 - Size.png"));

        // Теперь сделаем её чёрно-белой. Для этого поступим так:
        // Создадим новую пустую картинку нужных размеров, заранее указав последним
        // параметром чёрно-белую цветовую палитру:
        BufferedImage bwImg = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_BYTE_GRAY);
        // Попросим у этой картинки инструмент для рисования на ней:
        Graphics2D graphics = bwImg.createGraphics();
        // А этому инструменту скажем, чтобы он скопировал содержимое из нашей суженной картинки:
        graphics.drawImage(scaledImage, 0, 0, null);

        // отладка - вывод чёрно-белой картинки
//        ImageIO.write(bwImg, "png", new File("3 - Gray.png"));

        // Теперь в bwImg у нас лежит чёрно-белая картинка нужных нам размеров.
        // Вы можете отслеживать каждый из этапов, в любом удобном для
        // вас моменте сохранив промежуточную картинку в файл через:
        // ImageIO.write(imageObject, "png", new File("out.png"));
        // После вызова этой инструкции у вас в проекте появится файл картинки out.png

        // Теперь давайте пройдёмся по пикселям нашего изображения.
        // Если для рисования мы просили у картинки .createGraphics(),
        // то для прохода по пикселям нам нужен будет этот инструмент:
        WritableRaster bwRaster = bwImg.getRaster();

        // Он хорош тем, что у него мы можем спросить пиксель на нужных
        // нам координатах, указав номер столбца (w) и строки (h)
        // int color = bwRaster.getPixel(w, h, new int[3])[0];
        // Выглядит странно? Согласен. Сам возвращаемый методом пиксель — это
        // массив из трёх интов, обычно это интенсивность красного, зелёного и синего.
        // Но у нашей чёрно-белой картинки цветов нет, и нас интересует
        // только первое значение в массиве. Ещё мы параметром передаём интовый массив на три ячейки.
        // Дело в том, что этот метод не хочет создавать его сам и просит
        // вас сделать это, а сам метод лишь заполнит его и вернёт.
        // Потому что создавать массивы каждый раз слишком медленно. Вы можете создать
        // массив один раз, сохранить в переменную и передавать один
        // и тот же массив в метод, ускорив тем самым программу.

        // Вам осталось пробежаться двойным циклом по всем столбцам (ширина)
        // и строкам (высота) изображения, на каждой внутренней итерации
        // получить степень белого пикселя (int color выше) и по ней
        // получить соответствующий символ c. Логикой превращения цвета
        // в символ будет заниматься другой объект, который мы рассмотрим ниже
        int[] iArray = new int[3];
        StringBuilder buffer = new StringBuilder();
        System.out.printf("w = %d, h = %d\n", bwRaster.getWidth(), bwRaster.getHeight());
        for (int h = 0; h < bwRaster.getHeight(); h++) {
            for (int w = 0; w < bwRaster.getWidth(); w++) {
                int color = bwRaster.getPixel(w, h, iArray)[0];
                char c = textColorSchema.convert(color);
                //запоминаем символ c, например, в двумерном массиве или как-то ещё на ваше усмотрение
                buffer.append(c);
            }
            buffer.append("\n");
        }

        // Осталось собрать все символы в один большой текст.
        // Для того, чтобы изображение не было слишком узким, рекомендую
        // каждый пиксель превращать в два повторяющихся символа, полученных
        // от схемы.

//        // отладка - вывод картинки в текст
//        try(FileOutputStream fos = new FileOutputStream("4 - Text.txt")){
//            fos.write(buffer.toString().getBytes());
//        }catch(IOException e){
//
//        }


        return buffer.toString(); // Возвращаем собранный текст.
    }
}
