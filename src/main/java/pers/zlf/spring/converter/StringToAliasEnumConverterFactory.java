package pers.zlf.spring.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import pers.zlf.spring.enumeration.AliasEnum;
import pers.zlf.spring.enumeration.AliasEnumUtil;

public class StringToAliasEnumConverterFactory<R extends AliasEnum> implements ConverterFactory<String, R> {
    @Override
    public <T extends R> Converter<String, T> getConverter(Class<T> targetType) {
        return new StringToAliasEnum<>(targetType);
    }

    private class StringToAliasEnum<R extends AliasEnum> implements Converter<String, R> {
        private Class<R> type;

        private StringToAliasEnum(Class<R> type) {
            this.type = type;
        }

        @Override
        public R convert(String s) {
            return s.isEmpty() ? null : AliasEnumUtil.valueOf(type, s);
        }
    }

}
