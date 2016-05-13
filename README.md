# Projeto de Sistemas Distribuídos 2015-2016 #

Grupo de SD 70 - Campus Taguspark


Daniel Reigada 82064 daniel.reigada@gmail.com

Diogo Mesquita 81968 d.mesquita285@gmail.com

João Plancha 51355 joaoplancha@gmail.com


Repositório:
[tecnico-softeng-distsys-2015/T_70-project](https://github.com/tecnico-softeng-distsys-2015/T_70-project/)

-------------------------------------------------------------------------------

## Instruções de instalação


### Linux

[0] Iniciar sistema operativo


[1] Iniciar servidores de apoio

JUDDI:
```
JUDDIPATH/bin/startup.sh
```

[2] Criar pasta temporária

```
mkdir Upa70
cd Upa70
```


[3] Obter código fonte do projeto (versão entregue)

```
git clone https://github.com/tecnico-softeng-distsys-2015/T_70-project/ .
```


[4] Instalar módulos de bibliotecas auxiliares

**Uddi-naming:**
```
cd lib/uddi-naming/
mvn clean install
```
**SOAP handlers:**
```
cd lib/ws-handlers/
mvn clean install
```

-------------------------------------------------------------------------------

### Serviço TRANSPORTER

[1] Construir e executar testes unitários do **servidor**

```
cd transporter-ws/
mvn clean compile
mvn test
```

[2] Executar dois **servidores** (em terminais separados)

```
mvn exec:java -Dws.i=1
mvn exec:java -Dws.i=2
```

[3] Construir, instalar (necessário para o serviço broker) e correr testes de integração do **cliente** (noutro terminal)

```
cd transporter-ws-cli/
mvn clean install
```

-------------------------------------------------------------------------------

### Serviço BROKER

[1] Construir e executar testes de unitários do **servidor**

```
cd broker-ws/
mvn clean compile
mvn test
```

[2] Executar dois **transporters** (como referido no ponto 2 do serviço TRANSPORTER)

```
mvn exec:java -Dws.i=1
mvn exec:java -Dws.i=2
```

[3] Executar o **servidor de backup**

```
mvn exec:java -P backup-broker
```
[4] Esperar que o **servidor de backup** inicie e  executar o **servidor principal** (noutro terminal)
```
mvn exec:java
```

[5] Construir **cliente** e executar testes de integração (noutro terminal)

```
cd broker-ws-cli/
mvn clean compile
mvn verify
```

Nota: Caso queira correr uma simples interface de cliente para comunicar com o broker
```
cd broker-ws-cli/
mvn exec:java
```

-------------------------------------------------------------------------------
**FIM**
