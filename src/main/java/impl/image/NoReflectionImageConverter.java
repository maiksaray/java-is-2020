package impl.image;

import api.image.ImageConverter;

import java.awt.*;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.function.Function;

public class NoReflectionImageConverter implements ImageConverter {
    /**
     * Transforms 2d array of type S to 2d array of type T
     *
     * @param <S> Source type
     * @param <T> Target type
     */
    private static class ArrayTransformer<S, T> {

        /**
         * Transforms 2d array of type S to 2d array of type T using given converter function that is applied element-wise
         *
         * @param source      source 2d array of type S
         * @param converter   converter function that is applied to each element
         * @param targetClass Class object of target type, needed to be able to access target ctor cause of type erasure.
         * @return converted 2d array of type T
         * @implNote probably not the best looking impl since I can imagine same s possible using streams but
         * I couldn't get that right...
         */
        @SuppressWarnings("unchecked")
        public T[][] transform(S[][] source, Function<S, T> converter, Class<T> targetClass) {
//            T[][] result = new T[source.length][]; //oh, yeah, type erasure, I can't do this...
//            Ok, there's sorta workaround we can pass target class obj...
//            And since we need nested Arrays... and Class<T[]> is not a thing.....
            Class targetArrayClass = Array.newInstance(targetClass, 0).getClass();
            T[][] result = (T[][]) Array.newInstance(targetArrayClass, source.length);
            int rowIndex = 0;
            for (var row : source) {
                result[rowIndex] = (T[]) Array.newInstance(targetClass, row.length);
                int colIndex = 0;
                for (var item : row) {
                    result[rowIndex][colIndex] = converter.apply(item);
                    colIndex += 1;
                }
                rowIndex += 1;
            }
            return result;
        }
    }

    private Integer[][] box2dArray(int[][] array) {
        return Arrays.stream(array).map(
                (line) -> Arrays.stream(line).boxed().toArray(Integer[]::new)
        ).toArray(Integer[][]::new);
    }


    private int[][] unbox2dArray(Integer[][] array) {
        return Arrays.stream(array).map(
                line -> Arrays.stream(line).mapToInt(Integer::intValue).toArray()
        ).toArray(int[][]::new);
    }

    @Override
    public Color[][] convertToColor(int[][] image) {
//        return new ArrayTransformer<Integer, Color>().transform(image, this::constructColor, Color.class);
//        DAMN, primitives can't be generic params! second bump =(
//        How do I squeeze damn ints into all the generic thing without second n^2 iteration...
//        screw this, I'm doing copy-paste... OR double iterating for boxing-unboxing... how do you...
//        That's why I don't like java that much... Too many inconveniences
        Function<Integer, Color> colorValueSetter = Color::new;
        return new NoReflectionImageConverter.ArrayTransformer<Integer, Color>().transform(box2dArray(image), colorValueSetter, Color.class);
    }

    @Override
    public int[][] convertToRgb(Color[][] image) {
        Function<Color, Integer> colorValueGetter = Color::getRGB;
        return unbox2dArray(new NoReflectionImageConverter.ArrayTransformer<Color, Integer>().transform(image, colorValueGetter, Integer.class));
    }
}
