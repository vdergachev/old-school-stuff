INSERT INTO gklients_email_template VALUES ('95f0e39d-a29b-f818-61e2-2f88b3ae5700', '2017-08-05 22:32:11.281', 'admin', 1, '2017-08-05 22:32:11.281', NULL, NULL, NULL, '-', '<a style="margin: 0 0 10px; text-decoration: none; color: black; display: inline-block; border-bottom: 1px solid black;" href="#n[NEWS_ID]">[NEWS_TITLE]</a>', 8, 'HTML-разметка для составления содержания письма новостной рассылки');
INSERT INTO gklients_email_template VALUES ('bab5bac6-2c57-f588-1678-2c83cf6b9224', '2017-08-05 22:35:15.949', 'admin', 1, '2017-08-05 22:35:15.949', NULL, NULL, NULL, '-', '<tr>
<td style="padding: 60px 0 30px; line-height: 24px;">
[DISTRIBUTION_INTRO_TEXT]
</td>
</tr>', 10, 'HTML-разметка для вводного текста в новостную рассылку');
INSERT INTO gklients_email_template VALUES ('5b8820cb-9a2a-3877-48fc-be6b364e27f6', '2017-08-05 22:51:36.742', 'admin', 1, '2017-08-05 22:51:36.742', NULL, NULL, NULL, '-', '<span style="padding: 6px; background: white; border: 1px solid #d8d1d5; border-radius: 8px; display: inline-block;">[CLIENT_TAX]</span>', 12, 'HTML-разметка для режима налогообложения клиента в новостную рассылку');
INSERT INTO gklients_email_template VALUES ('1ee28cb9-7393-752c-e5a4-f5632acaed91', '2017-08-05 22:33:58.261', 'admin', 2, '2017-08-05 22:51:57.614', 'admin', NULL, NULL, '-', '<span style="padding: 6px; background: white; border: 1px solid #d8d1d5; border-radius: 8px; display: inline-block;">[CLIENT_STATUS]</span>', 9, 'HTML-разметка для статуса налогообложения клиента в новостной рассылке');
INSERT INTO gklients_email_template VALUES ('f80f56ce-75cf-f3af-2525-007c1af7a719', '2017-08-05 22:53:36.31', 'admin', 1, '2017-08-05 22:53:36.31', NULL, NULL, NULL, '-', '<html lang="ru"><head>
        <meta charset="utf-8">
        <style>
            p {
                margin:     0 0 10px;
            }
            a {
                color:              black;
                border-bottom:      1px solid #636363;
                text-decoration:    none;
            }
        </style>
    </head>
    <body>
        <center style="max-width: 760px; width: 100%; margin: 0 auto;">
        <table style="font: 16px Arial, Tahoma, Verdana, sans-serif;" cellpadding="0" cellspacing="0">
            <tbody><tr>
                <td style="padding: 50px 0;">
                    <img style="float: left;" width="221" height="66" alt="" border="0" src="http://glavkniga.ru/template/images/mail_gk.jpg">
                    <div style="float: right; width: 200px; font-size: 13px;">Ежедневная новостная рассылка для бухгалтера</div>
                </td>
            </tr>
            <tr>
                <td style="border: 1px solid black;">
                    <table style="width: 100%;" cellpadding="0" cellspacing="0">
                        <tbody><tr>
                            <td style="padding:40px 40px 20px; background: #ebe9ee;" align="right">
                                <b>[DATE]</b>
                            </td>
                        </tr>
                        <tr>
                            <td style="padding:0 40px 20px; background: #ebe9ee;" align="center">
                                <img width="97" height="97" src="http://glavkniga.ru/template/images/mail_logo.png">
                            </td>
                        </tr>
                        <tr>
                            <td style="padding: 0 40px 18px; background: #ebe9ee; font-size: 30px;" align="center">
                                БУХГАЛТЕРСКИЕ НОВОСТИ
                            </td>
                        </tr>
                        <tr>
                            <td style="padding: 0 40px 4px; background: #ebe9ee; font-size: 13px;" align="center">
                                [STATUS]
                            </td>
                        </tr>
                        <tr>
                            <td style="padding: 0 40px 4px; background: #ebe9ee; font-size: 13px;" align="center">
                              [TAX] 
                            </td>
                        </tr>
                        <tr>
                            <td style="padding: 0 40px 40px; background: #ebe9ee;" align="center">
                                <a style="font-size: 13px; color: #636363;  border-bottom: 1px dashed #636363; text-decoration: none;" href="https://gk.glavkniga.ru/#wrapper_news" target="_blank">Зарегистрироваться и настроить рассылку</a>
                            </td>
                        </tr>
                    </tbody></table>
					
                    <table style="margin: 0 80px 30px; text-align: left;" cellpadding="0" cellspacing="0">
                        <tbody>
						[INTRO]
                        <tr>
                            <td style="padding: 0 0 20px;">
                                <span style="font-size: 18px; color: white; background: #673180; padding: 4px 8px; float: left;"><b>Коротко</b></span>
                            </td>
                        </tr>
                        <tr>
                            <td style="font-size: 13px; padding: 0 0 10px;">
                                [ANNOTATION]
                            </td>
                        </tr>
                        <tr>
                            <td style="font-size: 13px; height: 10px; background: #ebe8ee;"></td>
                        </tr>
                        <tr>
                            <td style="padding: 40px 0 0;">
                                <span style="font-size: 18px; color: white; background: #673180; padding: 4px 8px; float: left;"><b>Подробно</b></span>
                            </td>
                        </tr>
                        [NEWS]
                        <tr>
                            <td style=""></td>
                        </tr>
                    </tbody></table>
                </td>
            </tr>
            <tr>
                <td style="padding: 40px 0 15px; font-size: 13px;" align="center">
                    Вы получили это письмо, потому что подписались на рассылку издательства
                    <a style="color: black; text-decoration: none; border-bottom: 1px solid black;" href="http://glavkniga.ru">«Главная книга»</a>
                </td>
            </tr>
            <tr>
                <td style="padding: 0 0 40px; font-size: 13px;" align="center">
                    Чтобы перестать получать все важные бухгалтерские новости, перейдите по
                    <a style="color: black; text-decoration: none; border-bottom: 1px solid black;" href="http://gk.glavkniga.ru/unsubscribe">ссылке</a>
                </td>
            </tr>
        </tbody></table>
        </center>
    
</body></html>', 11, 'Шаблон регулярной новостной рассылки');
INSERT INTO gklients_email_template VALUES ('d419e17f-f0ec-20fd-6ed7-7a68db098c48', '2017-08-05 22:30:28.5', 'admin', 2, '2017-08-05 23:16:20.786', 'admin', NULL, NULL, '-', '<tr id="n[NEWS_ID]">
	<td style="padding: 40px 0 20px;" align="center">
		<img width="360" src="http://glavkniga.ru/images/newsline_images/[NEWS_IMG_URL]">
	</td>
</tr>
<tr>
	<td style="padding: 0 0 20px; font-size: 30px;">
		<a style="text-decoration: none; color: black; border-bottom: 1px solid black;" href="http://glavkniga.ru/news/[NEWS_ID]" target="_blank">
			<b>[NEWS_TITLE]</b>
		</a>
	</td>
</tr>
<tr>
	<td style="padding: 0 0 10px; font-weight: bold; line-height: 24px;">
		[NEWS_LEADTEXT]
	</td>
</tr>
<tr>
	<td style="padding: 0 0 40px; line-height: 24px;">
		[NEWS_FULLTEXT]
	</td>
</tr>
<tr>
	<td style="font-size: 13px; height: 10px; background: #ebe8ee;"></td>
</tr>', 7, 'HTML-разметка для внесения в неё новости в новостной рассылке');
