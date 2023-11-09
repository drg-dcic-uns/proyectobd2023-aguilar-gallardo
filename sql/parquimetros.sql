CREATE DATABASE parquimetros;

USE parquimetros;

CREATE TABLE conductores(
    dni INT unsigned NOT NULL,
    registro INT unsigned NOT NULL,
    nombre VARCHAR(33) NOT NULL,
    apellido VARCHAR(33) NOT NULL,
    direccion VARCHAR(33) NOT NULL,
    telefono VARCHAR(20),

    CONSTRAINT pk_conductores
    PRIMARY KEY (dni)

)ENGINE=InnoDB;

CREATE TABLE automoviles(
    patente VARCHAR(6) NOT NULL,
    marca VARCHAR(33) NOT NULL,
    modelo VARCHAR(33) NOT NULL,
    color VARCHAR(33) NOT NULL,
    dni INT unsigned NOT NULL,

    CONSTRAINT pk_automoviles
    PRIMARY KEY (patente),

    CONSTRAINT FK_automoviles_conductor
    FOREIGN KEY (dni) REFERENCES conductores (dni) 
    ON DELETE RESTRICT ON UPDATE CASCADE
)ENGINE=InnoDB;

CREATE TABLE tipos_tarjeta (
    tipo VARCHAR(33) NOT NULL,
    descuento DECIMAL(3,2) unsigned CHECK (descuento >= 0 AND descuento <= 1) NOT NULL,

    CONSTRAINT pk_tipos_tarjeta
    PRIMARY KEY (tipo)


)ENGINE=InnoDB;

CREATE TABLE tarjetas(
    id_tarjeta INT unsigned NOT NULL AUTO_INCREMENT,
    saldo DECIMAL(5,2) NOT NULL DEFAULT 0,
    patente VARCHAR(6) NOT NULL,
    tipo VARCHAR(33) NOT NULL,

    CONSTRAINT pk_tarjetas
    PRIMARY KEY (id_tarjeta),

    CONSTRAINT FK_tajeta_automoviles
    FOREIGN KEY (patente) REFERENCES automoviles (patente) 
    ON DELETE RESTRICT ON UPDATE CASCADE,
    
    CONSTRAINT FK_tajeta_tipo_tarjeta
    FOREIGN KEY (tipo) REFERENCES tipos_tarjeta (tipo) 
    ON DELETE RESTRICT ON UPDATE CASCADE
)ENGINE=InnoDB;


CREATE TABLE recargas(
    fecha DATE NOT NULL,
    hora TIME NOT NULL,
    saldo_anterior DECIMAL(5,2) NOT NULL DEFAULT 0,
    saldo_posterior DECIMAL(5,2) NOT NULL DEFAULT 0,
    id_tarjeta INT unsigned NOT NULL,

    CONSTRAINT pk_recargas
    PRIMARY KEY(id_tarjeta,fecha,hora),
    
    CONSTRAINT FK_recargas_tarjetas
    FOREIGN KEY (id_tarjeta) REFERENCES tarjetas(id_tarjeta)
    ON DELETE RESTRICT ON UPDATE CASCADE



)ENGINE=InnoDB;

CREATE TABLE inspectores(
    legajo INT unsigned NOT NULL,
    dni INT unsigned NOT NULL,
    nombre VARCHAR(33) NOT NULL,
    apellido VARCHAR(33) NOT NULL,
    password VARCHAR(32) NOT NULL,

    CONSTRAINT pk_inspectores
    PRIMARY KEY(legajo)

)ENGINE=InnoDB;

CREATE TABLE ubicaciones(
    calle VARCHAR(33) NOT NULL,
    altura SMALLINT unsigned NOT NULL,
    tarifa DECIMAL(5,2) unsigned NOT NULL,

    CONSTRAINT pk_ubicaciones
    PRIMARY KEY(calle,altura)

)ENGINE=InnoDB;

CREATE TABLE parquimetros(
    id_parq INT unsigned NOT NULL,
    numero INT unsigned NOT NULL,
    calle VARCHAR(33) NOT NULL,
    altura SMALLINT unsigned NOT NULL,
    
    CONSTRAINT pk_parquimetros
    PRIMARY KEY(id_parq),

    CONSTRAINT FK_parquimetros_ubicaciones
    FOREIGN KEY (calle,altura) REFERENCES ubicaciones(calle,altura)
    ON DELETE RESTRICT ON UPDATE CASCADE
    
)ENGINE=InnoDB;

CREATE TABLE asociado_con(
    id_asociado_con INT unsigned NOT NULL AUTO_INCREMENT,
    dia ENUM ('do','lu','ma','mi','ju','vi','sa') NOT NULL,
    turno ENUM ('m','t') NOT NULL,
    legajo INT unsigned NOT NULL,
    calle VARCHAR(33) NOT NULL,
    altura SMALLINT unsigned NOT NULL,
    

    CONSTRAINT FK_asociado_con_inspector
    FOREIGN KEY (legajo) REFERENCES inspectores(legajo)
    ON DELETE RESTRICT ON UPDATE CASCADE,

    CONSTRAINT FK_asociado_con_ubicaciones
    FOREIGN KEY (calle,altura) REFERENCES ubicaciones(calle,altura)
    ON DELETE RESTRICT ON UPDATE CASCADE,
    
    CONSTRAINT pk_asociado_con
    PRIMARY KEY (id_asociado_con)

)ENGINE=InnoDB;

CREATE TABLE multa(
    numero INT unsigned NOT NULL AUTO_INCREMENT,
    fecha DATE NOT NULL,
    hora TIME NOT NULL,
    id_asociado_con INT unsigned NOT NULL,
    patente VARCHAR(6) NOT NULL,

    CONSTRAINT FK_multa_automoviles
    FOREIGN KEY (patente) REFERENCES automoviles(patente)
    ON DELETE RESTRICT ON UPDATE CASCADE,

    CONSTRAINT FK_multa_asociado_con
    FOREIGN KEY (id_asociado_con) REFERENCES asociado_con(id_asociado_con)
    ON DELETE RESTRICT ON UPDATE CASCADE,

    CONSTRAINT pk_multa
    PRIMARY KEY (numero)

)ENGINE=InnoDB;


CREATE TABLE accede(
    fecha DATE NOT NULL,
    hora TIME NOT NULL,
    id_parq INT unsigned NOT NULL,
    legajo INT unsigned NOT NULL,

    CONSTRAINT FK_accede_parquimetros
    FOREIGN KEY (id_parq) REFERENCES parquimetros(id_parq)
    ON DELETE RESTRICT ON UPDATE CASCADE,

    CONSTRAINT FK_accede_inspectores
    FOREIGN KEY (legajo) REFERENCES inspectores(legajo)
    ON DELETE RESTRICT ON UPDATE CASCADE,

    CONSTRAINT pk_accede
    PRIMARY KEY (id_parq, fecha, hora)
)ENGINE=InnoDB;

CREATE TABLE estacionamientos(
    fecha_ent DATE NOT NULL,
    hora_ent TIME NOT NULL,
    fecha_sal DATE,
    hora_sal TIME,
    id_tarjeta INT unsigned NOT NULL,
    id_parq INT unsigned NOT NULL,

    CONSTRAINT pk_estacionamientos
    PRIMARY KEY(id_parq,fecha_ent,hora_ent),

    CONSTRAINT FK_estacionamientos_tarjetas
    FOREIGN KEY (id_tarjeta) REFERENCES tarjetas(id_tarjeta)
    ON DELETE RESTRICT ON UPDATE CASCADE,

    CONSTRAINT FK_estacionamientos_parquimetros
    FOREIGN KEY (id_parq) REFERENCES parquimetros(id_parq)
    ON DELETE RESTRICT ON UPDATE CASCADE

)ENGINE=InnoDB;

CREATE VIEW estacionados AS
SELECT
    p.calle,
    p.altura,
    t.patente,
    e.fecha_ent,
    e.hora_ent
FROM
    (estacionamientos AS e NATURAL JOIN tarjetas AS t) NATURAL JOIN parquimetros AS p 
WHERE
    e.fecha_sal IS NULL AND e.hora_sal IS NULL;

use parquimetros;
delimiter ! # define “!” como delimitador
create procedure conectar(IN IDtarjeta INTEGER , IN IDparq INTEGER)
    begin
        declare fecha_salida, fecha_entrada DATE;
        declare hora_salida, hora_entrada TIME;
        declare tipoOperacion VARCHAR(10);
        declare tiempo DECIMAL(5,2);
        declare tarifaParquimetro DECIMAL(5,2);
        declare descuentoTarjeta DECIMAL(3,2);
        declare saldoTarjeta DECIMAL(5,2);

        SELECT fecha_sal, hora_sal, fecha_ent, hora_ent INTO fecha_salida, hora_salida, fecha_entrada, hora_entrada FROM estacionamientos WHERE id_tarjeta=IDtarjeta and id_parq=IDparq;
    
        SELECT saldo INTO saldoTarjeta FROM tarjetas WHERE id_tarjeta=IDtarjeta;

        declare alturaUbicacion SMALLINT;
        declare calleUbicacion VARCHAR(33);

        SELECT calle, altura INTO calleUbicacion, alturaUbicacion FROM parquimetros WHERE id_parq=IDparq;
        SELECT tarifa INTO tarifaParquimetro FROM ubicaciones WHERE calle=calleUbicacion and altura=alturaUbicacion;

        declare tipoTarjeta VARCHAR(33);
        SELECT tipo INTO tipoTarjeta FROM tarjetas WHERE id_tarjeta=IDtarjeta;
        SELECT descuento INTO descuentoTarjeta FROM tipos_tarjeta WHERE tipo=tipoTarjeta;

        if (fecha_entrada IS NOT NULL and hora_entrada IS NOT NULL fecha_sal IS NULL and hora_sal IS NULL) THEN
            set tipoOperacion = "apertura";
            declare fecha_actual DATE;
            declare hora_actual TIME;

            SELECT CURDATE() INTO fecha_actual;     #calculo la diferencia de minutos
            SELECT CURTIME() INTO hora_actual;
            declare diferenciaFechas, diferenciaHoras TIME;
            SELECT TIMEDIFF(fecha_entrada, fecha_actual) INTO diferenciaFechas;
            SELECT TIMEDIFF(fecha_entrada, fecha_actual) INTO diferenciaHoras;
            set tiempo = (TIME_TO_SEC(diferenciaFechas) + TIME_TO_SEC(diferenciaHoras))/60;

            set saldoTarjeta = saldoTarjeta−(tiempo∗tarifaParquimetro∗(1−descuentoTarjeta)),

            update tarjetas # actualizo el saldo de la tarjeta
            set saldo = saldoTarjeta
            where id_tarjeta = IDtarjeta;

            update estacionamientos # actualizo el saldo de la tarjeta
            set  fecha_sal=fecha_actual, hora_sal=hora_actual
            where id_tarjeta = IDtarjeta and id_parq=IDparq;

            SELECT tipoOperacion as Tipo_Operacion, tiempo as Cantidad_Tiempo_Minutos, saldoTarjeta as Saldo_Actualizado, fecha_entrada as Fecha_Apertura, hora_entrada as Hora_Apertura, fecha_salida as Fecha_Cierre, hora_salida as Hora_Cierre;
        ELSE
            set tipoOperacion = "cierre";

        END IF;
    end; !
delimiter ; # reestablece el “;” como delimitador


#-----------------------------------------------------------------------------------
#Creacion de usuarios y otorgamiento de privilegios
    
#Usuario admin_parquimetros    
    CREATE USER 'admin'@'localhost' IDENTIFIED BY 'admin';

    GRANT ALL PRIVILEGES ON parquimetros.* TO  'admin'@'localhost' WITH GRANT OPTION;

    GRANT CREATE USER ON *.* TO 'admin'@'localhost';

#Usuario venta_parquimetros
    CREATE USER 'venta'@'%' IDENTIFIED BY 'venta';

    GRANT INSERT ON parquimetros.tarjetas TO 'venta'@'%';
    GRANT SELECT(id_tarjeta) ON parquimetros.tarjetas TO 'venta'@'%';
    GRANT UPDATE(saldo) ON parquimetros.tarjetas TO 'venta'@'%';

#Usuario inspector_parquimetros
    CREATE USER 'inspector'@'%' IDENTIFIED BY 'inspector';

    GRANT SELECT ON parquimetros.inspectores TO 'inspector'@'%';
    GRANT SELECT ON parquimetros.multa TO 'inspector'@'%';          #le agregamos este permiso para que pueda sacar el numero de multa puesto automatico en el método registrar multa
    GRANT SELECT(patente) ON parquimetros.automoviles TO 'inspector'@'%';
    GRANT SELECT ON parquimetros.estacionados TO 'inspector'@'%';
    GRANT SELECT ON parquimetros.parquimetros TO 'inspector'@'%';
    GRANT INSERT ON parquimetros.multa TO 'inspector'@'%';
    GRANT INSERT ON parquimetros.accede TO 'inspector'@'%';
    GRANT SELECT ON parquimetros.asociado_con TO 'inspector'@'%';

#Usuario parquimetro
    CREATE USER 'parquimetro'@'%' IDENTIFIED BY 'parq';
    GRANT EXECUTE ON procedure parquimetros.conectar TO 'parquimetro'@'%' ;


