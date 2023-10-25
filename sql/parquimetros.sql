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




