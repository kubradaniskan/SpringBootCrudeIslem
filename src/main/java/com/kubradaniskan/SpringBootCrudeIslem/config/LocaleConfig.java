package com.kubradaniskan.SpringBootCrudeIslem.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.util.Locale;

// Bu sınıf API'nin döndürdüğü mesajların kullanıcının dil tercihlerine göre ayarlanmasını sağlıyor.

@Configuration
public class LocaleConfig {

    @Bean
    public LocaleResolver localeResolver() {
        AcceptHeaderLocaleResolver resolver = new AcceptHeaderLocaleResolver();
        resolver.setDefaultLocale(new Locale("tr")); // Varsayılan Türkçe
        return resolver;
    }

    //messageSource çoklu dil desteği için kullanıldı.
    //ResourceBundleMessageSource, dosyalardaki anahtar-değer çiftlerinden mesajları çeker.
    @Bean
    public ResourceBundleMessageSource messageSource()
    { //messagesour ile error.database.connection gibi hatalar çağrılıp ilgili dilde mesaj alınıyor.
        ResourceBundleMessageSource source = new ResourceBundleMessageSource();
        source.setBasename("messages");  //messages adıyla olan property dosyaları okunuyor
        source.setDefaultEncoding("UTF-8");
        return source;
    }
}
