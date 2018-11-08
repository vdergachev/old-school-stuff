/*
 * Copyright (c) 2017 gklients
 */
package ru.glavkniga.gklients.service;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.haulmont.cuba.core.global.Messages;
import org.springframework.stereotype.Service;
import ru.glavkniga.gklients.crudentity.DailyNewsDistribution;
import ru.glavkniga.gklients.crudentity.GKNews;
import ru.glavkniga.gklients.entity.ClientDistributionSettings;
import ru.glavkniga.gklients.entity.EmailTemplate;
import ru.glavkniga.gklients.enumeration.SysEmail;

import javax.inject.Inject;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author IgorLysov
 */
@Service(NewsTemplateProcessorService.NAME)
public class NewsTemplateProcessorServiceBean implements NewsTemplateProcessorService {

    private final Token newsTitle = new Token("NEWS_TITLE");
    private final Token newsId = new Token("NEWS_ID");
    private final Token newsImgUrl = new Token("NEWS_IMG_URL");
    private final Token newsLeadtext = new Token("NEWS_LEADTEXT");
    private final Token newsFulltext = new Token("NEWS_FULLTEXT");

    //These tokens have multiple values for each client. Think of how to fill them in


    private final Token clientStatus = new Token("CLIENT_STATUS");
    private final Token clientTax = new Token("CLIENT_TAX");

    private final Token distributionIntroText = new Token("DISTRIBUTION_INTRO_TEXT");

    //final tokens to insert into template
    private Token templateDate;
    private Token templateStatus;
    private Token templateTax;
    private Token templateIntro;
    private Token templateAnnotation;
    private Token templateNews;

    //template
    private Token finalTemplate;

    @Inject
    private EmailerService emailerService;
    // to call private EmailTemplate loadTemplate(SysEmail sysEmail);
    // to call private String processTemplate(String template, HashMap<String, String> replacements);

    @Inject
    private Messages messages;
    //


    @Override
    public String process(ClientDistributionSettings settings, List<GKNews> newsList, DailyNewsDistribution dnd) {
        initTokens();
        completeDailyNewsTokens(dnd);
        completeClientTokens(settings);

        String templateStatusProcessedTemplate = processMultipleValues(templateStatus.template, clientStatus);
        templateStatus.addValue(templateStatusProcessedTemplate);
        String templateTaxProcessedTemplate = processMultipleValues(templateTax.template, clientTax);
        templateTax.addValue(templateTaxProcessedTemplate);
        String templateIntroProcessedTemplate = processTemplate(templateIntro.template, distributionIntroText);
        templateIntro.addValue(templateIntroProcessedTemplate);
        for (GKNews news : newsList) {
            // STUB for "other news" based on customer requirement: don't need to add full text pf Other news to email body.
            if (news.getIsOther()) {
                news.setFullText("");
            }
            completeNewsTokens(news);
            String templateNewsProcessedTemplate = processTemplate(templateNews.template, newsId, newsImgUrl, newsTitle, newsLeadtext, newsFulltext);
            templateNews.addValue(templateNewsProcessedTemplate);
            String templateAnnotationProcessedTemplate = processTemplate(templateAnnotation.template, newsTitle, newsId);
            templateAnnotation.addValue(templateAnnotationProcessedTemplate);
        }
        compressValues(templateNews);
        compressValues(templateAnnotation);

        return processTemplate(finalTemplate.template, templateDate, templateStatus, templateTax, templateIntro, templateAnnotation, templateNews);
    }

    private void initTokens() {

        //final tokens to insert into template
        this.templateDate = new Token("DATE");
        this.templateStatus = new Token("STATUS", SysEmail.NEWS_STATUS);
        this.templateTax = new Token("TAX", SysEmail.NEWS_TAX);
        this.templateIntro = new Token("INTRO", SysEmail.NEWS_INTRO);
        this.templateAnnotation = new Token("ANNOTATION", SysEmail.NEWS_ANNOTATION);
        this.templateNews = new Token("NEWS", SysEmail.NEWS_NEWS); //several values

        //template
        this.finalTemplate = new Token("-", SysEmail.NEWS_TEMPLATE);
    }

    private void completeDailyNewsTokens(DailyNewsDistribution dnd) {
        Date date = dnd.getDistributionDate();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy", myDateFormatSymbols);
        templateDate.addValue(dateFormat.format(date));
        distributionIntroText.addValue(dnd.getIntroText());
    }

    private void completeNewsTokens(GKNews news) {
        newsTitle.clearValues();
        newsId.clearValues();
        newsImgUrl.clearValues();
        newsLeadtext.clearValues();
        newsFulltext.clearValues();

        newsTitle.addValue(news.getTitle());
        newsId.addValue(String.valueOf(news.getSiteId()));

        if (news.getImages() != null && news.getImages().size() > 0) {
            newsImgUrl.addValue(news.getImages().iterator().next().getImage());
        } else {
            newsImgUrl.addValue("");
        }
        newsLeadtext.addValue(news.getLeadText());
        newsFulltext.addValue(news.getFullText());
    }

    private void compressValues(Token token) {
        String value = token.getValues().stream().collect(Collectors.joining());
        token.setValues(ImmutableList.of(value));
    }


    private void completeClientTokens(ClientDistributionSettings settings) {
        //   String msg;
        //  MessageTools messageTools = messages.getTools();

        clientTax.clearValues();
        //TODO define how call messages in Russian locale and get rid of russian chars hardcode

        //Complete Tax settings
        if (settings.getIsOsno() != null && settings.getIsOsno()) {
            //msg = messageTools.getPropertyCaption(settings.getMetaClass(), "isOsno");

            clientTax.addValue("ОСНО");
        }
        if (settings.getIsUsn() != null && settings.getIsUsn()) {
            //msg = messageTools.getPropertyCaption(settings.getMetaClass(), "isUsn");
            clientTax.addValue("УСН");
        }
        if (settings.getIsEnvd() != null && settings.getIsEnvd()) {
            //msg = messageTools.getPropertyCaption(settings.getMetaClass(), "isEnvd");
            clientTax.addValue("ЕНВД");
        }
        if (settings.getIsEshn() != null && settings.getIsEshn()) {
            //msg = messageTools.getPropertyCaption(settings.getMetaClass(), "isEshn");
            clientTax.addValue("ЕСХН");
        }
        if (settings.getIsPsn() != null && settings.getIsPsn()) {
            //msg = messageTools.getPropertyCaption(settings.getMetaClass(), "isPsn");
            clientTax.addValue("ПСН");
        }

        clientStatus.clearValues();
        //Complete Organization status
        if (settings.getIsCompany() != null && settings.getIsCompany()) {
            //msg = messageTools.getPropertyCaption(settings.getMetaClass(), "isCompany");
            clientStatus.addValue("Организация");
        }
        if (settings.getIsIp() != null && settings.getIsIp()) {
            //msg = messageTools.getPropertyCaption(settings.getMetaClass(), "isIp");
            clientStatus.addValue("ИП");
        }
        if (settings.getIsWithWorkers() != null && settings.getIsWithWorkers()) {
            //msg = messageTools.getPropertyCaption(settings.getMetaClass(), "isWithWorkers");
            clientStatus.addValue("С работниками");
        }
        if (settings.getIsWithoutWorkers() != null && settings.getIsWithoutWorkers()) {
            //msg = messageTools.getPropertyCaption(settings.getMetaClass(), "isWithoutWorkers");
            clientStatus.addValue("Без работников");
        }
        if (settings.getIsCivil() != null && settings.getIsCivil()) {
            //msg = messageTools.getPropertyCaption(settings.getMetaClass(), "isCivil");
            clientStatus.addValue("Граждане");
        }

    }

    private String processTemplate(String template, Token... tokens) {
        Map<String, String> replacements = new HashMap<>();
        for (Token token : tokens) {
            String value = token.getFirstValue() != null ? token.getFirstValue() : "";
            replacements.put(token.getName(), value);
        }
        return emailerService.processTemplate(template, replacements);
    }

    private String processMultipleValues(String template, Token token) {
        StringBuilder resultString = new StringBuilder();
        List<String> tokenValues = token.getValues();
        for (String tokenValue : tokenValues) {
            if (tokenValue == null) tokenValue = "";
            Map<String, String> singleReplacement = new ImmutableMap.Builder<String, String>().put(token.getName(), tokenValue).build();
            resultString.append(emailerService.processTemplate(template, singleReplacement));
        }
        return resultString.toString();
    }

    private static DateFormatSymbols myDateFormatSymbols = new DateFormatSymbols() {

        @Override
        public String[] getMonths() {
            return new String[]{"января", "февраля", "марта", "апреля", "мая", "июня",
                    "июля", "августа", "сентября", "октября", "ноября", "декабря"};
        }

    };


    private class Token {
        private String name;
        private SysEmail wrapperEnum;
        private List<String> values = new ArrayList<>();
        private String template;

        public String getName() {
            return name;
        }

        public void setValues(List<String> values) {
            this.values = values;
        }

        public void clearValues() {
            this.values.clear();
        }

        public List<String> getValues() {
            return values;
        }

        public String getFirstValue() {
            if (values != null)
                return values.iterator().next();
            else
                return null;
        }

        public int getValuesCount() {
            if (values != null)
                return values.size();
            else
                return 0;
        }

        public SysEmail getWrapperEnum() {
            return wrapperEnum;
        }

        public void addValue(String value) {
            this.values.add(value);
        }

        private Token(String name) {
            this.name = name;
        }

        private Token(String name, SysEmail wrapperEnum) {
            this.name = name;
            this.wrapperEnum = wrapperEnum;
            EmailTemplate templateObject = emailerService.loadTemplate(wrapperEnum);
            if (templateObject != null) {
                this.template = templateObject.getBody();
            }

        }
    }


}