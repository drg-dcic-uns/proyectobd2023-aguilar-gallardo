USE parquimetros;

INSERT INTO conductores VALUES (44321378, 133922, "Ezequiel", "Aguilar", "Alvarado 1875", "2914252047");
INSERT INTO conductores VALUES (44321380, 124298, "Julian", "Gallardo", "Argentino Sud 1846", "2914254567");
INSERT INTO conductores VALUES (44345378, 123922, "Tobias", "Gatti", "Eduardo Gonzalez 1575", "2915678709");
INSERT INTO conductores VALUES (44210378, 100922, "Manuel", "Gonzalez", "Brasil 1275", "2916789567");
INSERT INTO conductores VALUES (23321543, 111922, "Maximiliano", "Aguilar", "Alvarado 75", "2914262057");

INSERT INTO automoviles VALUES ("AHB987", "Nissan", "Kicks", "Gris", 44321378);
INSERT INTO automoviles VALUES ("HJF987", "Chevrolet", "Kicks", "Rojo", 44321378);
INSERT INTO automoviles VALUES ("AHJ546", "Nissan", "Frontier", "Gris", 44321380);
INSERT INTO automoviles VALUES ("JJH666", "Nissan", "Frontier", "Roja", 44345378);
INSERT INTO automoviles VALUES ("JLO111", "Gol", "209", "Gris", 44210378);
INSERT INTO automoviles VALUES ("AHB555", "Clio", "2019", "Blanco", 23321543);

INSERT INTO tipos_tarjeta VALUES ("Visa", 000.15);
INSERT INTO tipos_tarjeta VALUES ("Mastercard", 000.20);
INSERT INTO tipos_tarjeta VALUES ("Naranja", 000.30);

INSERT INTO tarjetas VALUES (546789, 700.00, "AHB987", "Visa");
INSERT INTO tarjetas VALUES (547423, 756.98, "AHB987", "Mastercard");
INSERT INTO tarjetas VALUES (876123, 134.24, "HJF987", "Naranja");
INSERT INTO tarjetas VALUES (546839, 777.11, "AHJ546", "Naranja");
INSERT INTO tarjetas VALUES (645456, 222.77, "JJH666", "Visa");
INSERT INTO tarjetas VALUES (132450, 333.23, "JLO111", "Visa");
INSERT INTO tarjetas VALUES (985765, 444.98, "AHB555", "Visa");

INSERT INTO recargas VALUES ('2022-01-12', '03:25:08', 876.98, 976.98, 546789);
INSERT INTO recargas VALUES ('2022-01-12', '03:45:08', 976.98, 986.98, 546789);
INSERT INTO recargas VALUES ('2022-02-15', '15:25:08', 756.98, 856.98, 547423);
INSERT INTO recargas VALUES ('2022-03-09', '16:25:08', 134.24, 234.24, 876123);
INSERT INTO recargas VALUES ('2022-04-02', '17:25:08', 777.11, 877.11, 546839);
INSERT INTO recargas VALUES ('2022-05-22', '18:25:08', 222.77, 322.77, 645456);
INSERT INTO recargas VALUES ('2022-06-17', '19:25:08', 333.23, 433.23, 132450);
INSERT INTO recargas VALUES ('2022-07-16', '20:25:08', 444.98, 544.98, 985765);

INSERT INTO inspectores VALUES (44876678, 123456, "Maximiliano", "Carrano", md5('inspector1'));
INSERT INTO inspectores VALUES (42654678, 111999, "Javier", "Carrano", md5('inspector2'));

INSERT INTO inspectores VALUES (76656234, 123456, "Hernesto", "Aguilar", md5('inspector3'));
INSERT INTO inspectores VALUES (32145678, 987678, "Tomas", "Gonzalez", md5('inspector4'));
INSERT INTO inspectores VALUES (12765678, 556788, "Tobias", "Gatti", md5('inspector5'));

INSERT INTO ubicaciones VALUES ("Alvarado", 1875, 080.43);
INSERT INTO ubicaciones VALUES ("Alvarado", 1567, 090.43);
INSERT INTO ubicaciones VALUES ("Eduardo Gonzalez", 1875, 020.43);
INSERT INTO ubicaciones VALUES ("Brasil", 1575, 3.43);
INSERT INTO ubicaciones VALUES ("Aguado", 1675, 120.43);

INSERT INTO parquimetros VALUES (3456, 89, "Alvarado", 1875);
INSERT INTO parquimetros VALUES (8765, 9, "Alvarado", 1567);
INSERT INTO parquimetros VALUES (0988, 10, "Eduardo Gonzalez", 1875);
INSERT INTO parquimetros VALUES (6789, 11, "Brasil", 1575);
INSERT INTO parquimetros VALUES (1332, 12, "Aguado", 1675);
INSERT INTO parquimetros VALUES (9999, 13, "Brasil", 1575);

INSERT INTO asociado_con VALUES (16883, 'do', 'm', 44876678, "Alvarado", 1875);
INSERT INTO asociado_con VALUES (16884, 'lu', 'm', 42654678, "Alvarado", 1567);
INSERT INTO asociado_con VALUES (NULL, 'ma', 't', 76656234, "Eduardo Gonzalez", 1875);
INSERT INTO asociado_con VALUES (NULL, 'mi', 't', 32145678, "Brasil", 1575);
INSERT INTO asociado_con VALUES (NULL, 'ju', 't', 12765678, "Aguado", 1675);
INSERT INTO asociado_con VALUES (NULL, 'ma', 't', 44876678, "Alvarado", 1875);
INSERT INTO asociado_con VALUES (NULL, 'ma', 't', 44876678, "Alvarado", 1567);
INSERT INTO asociado_con VALUES (NULL, 'ju', 'm', 44876678, "Alvarado", 1567);
INSERT INTO asociado_con VALUES (NULL, 'mi', 'm', 44876678, "Alvarado", 1567);

INSERT INTO multa VALUES (148, '2022-04-02', '18:25:08', 16883, "AHB987");
INSERT INTO multa VALUES (149,'2023-04-02', '18:27:08', 16884, "HJF987");
INSERT INTO multa VALUES (150,'2021-04-02', '18:28:08', 16885, "AHJ546");
INSERT INTO multa VALUES (NULL,'2020-04-02', '18:30:08', 16886, "JJH666");
INSERT INTO multa VALUES (NULL,'2019-04-02', '18:11:08', 16887, "JLO111");


INSERT INTO accede VALUES ('2017-04-02', '18:11:08', 3456, 44876678);
INSERT INTO accede VALUES ('2016-04-02', '18:11:08', 8765, 42654678);
INSERT INTO accede VALUES ('2015-04-02', '18:11:08', 0988, 76656234);
INSERT INTO accede VALUES ('2010-04-02', '18:11:08', 6789, 32145678);
INSERT INTO accede VALUES ('2010-04-02', '18:11:10', 6789, 12765678);

INSERT INTO estacionamientos VALUES ('2010-04-02', '18:11:10', '2010-04-02', '18:20:10', 546789, 3456);
INSERT INTO estacionamientos VALUES ('2023-11-28', '14:20:10', NULL, NULL, 547423, 8765);
INSERT INTO estacionamientos VALUES ('2023-11-28', '14:15:10', NULL, NULL, 876123, 0988);
INSERT INTO estacionamientos VALUES ('2023-11-28', '14:11:10', NULL, NULL, 546839, 6789);
INSERT INTO estacionamientos VALUES ('2014-04-02', '18:11:10', '2014-04-02', '18:20:10', 645456, 1332);
