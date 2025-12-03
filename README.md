# Groupe_24
Implémentation en Java de la solution MediPass: TP de fin de cours UML/POO du groupe 24 

# Installation et exécution
Pour commencer, clonez le dépôt GitHub et accédez-y:
```bash
git clone https://github.com/yourusername/Groupe_24.git
cd Groupe_24
```

## Téléchargement des dépendances
Avant de compiler, vous devez télécharger les dépendances requises. Créez d'abord un répertoire lib puis téléchargez les fichiers JAR (fichiers de dépendances)en exécutant les instructions suivantes :

```bash
mkdir lib
```

Ensuite, téléchargez les dépendances nécessaires :

### Sur Windows (PowerShell)
```bash
Invoke-WebRequest -Uri https://repo1.maven.org/maven2/tech/tablesaw/tablesaw-core/0.43.1/tablesaw-core-0.43.1.jar -OutFile lib/tablesaw-core-0.43.1.jar
Invoke-WebRequest -Uri https://repo1.maven.org/maven2/tech/tablesaw/tablesaw-jsplot/0.43.1/tablesaw-jsplot-0.43.1.jar -OutFile lib/tablesaw-jsplot-0.43.1.jar
Invoke-WebRequest -Uri https://repo1.maven.org/maven2/it/unimi/dsi/fastutil/8.5.12/fastutil-8.5.12.jar -OutFile lib/fastutil-8.5.12.jar
Invoke-WebRequest -Uri https://repo1.maven.org/maven2/io/github/classgraph/classgraph/4.8.165/classgraph-4.8.165.jar -OutFile lib/classgraph-4.8.165.jar
Invoke-WebRequest -Uri https://repo1.maven.org/maven2/com/univocity/univocity-parsers/2.9.1/univocity-parsers-2.9.1.jar -OutFile lib/univocity-parsers-2.9.1.jar
Invoke-WebRequest -Uri https://repo1.maven.org/maven2/org/slf4j/slf4j-api/1.7.36/slf4j-api-1.7.36.jar -OutFile lib/slf4j-api-1.7.36.jar
Invoke-WebRequest -Uri https://repo1.maven.org/maven2/org/slf4j/slf4j-simple/1.7.36/slf4j-simple-1.7.36.jar -OutFile lib/slf4j-simple-1.7.36.jar
Invoke-WebRequest -Uri https://repo1.maven.org/maven2/com/google/guava/guava/31.1-jre/guava-31.1-jre.jar -OutFile lib/guava-31.1-jre.jar
Invoke-WebRequest -Uri https://repo1.maven.org/maven2/com/ibm/icu/icu4j/72.1/icu4j-72.1.jar -OutFile lib/icu4j-72.1.jar
Invoke-WebRequest -Uri https://repo1.maven.org/maven2/org/apache/commons/commons-math3/3.6.1/commons-math3-3.6.1.jar -OutFile lib/commons-math3-3.6.1.jar
```

### Sur Linux/Mac (curl)
```bash
curl -L -o lib/tablesaw-core-0.43.1.jar https://repo1.maven.org/maven2/tech/tablesaw/tablesaw-core/0.43.1/tablesaw-core-0.43.1.jar
curl -L -o lib/tablesaw-jsplot-0.43.1.jar https://repo1.maven.org/maven2/tech/tablesaw/tablesaw-jsplot/0.43.1/tablesaw-jsplot-0.43.1.jar
curl -L -o lib/fastutil-8.5.12.jar https://repo1.maven.org/maven2/it/unimi/dsi/fastutil/8.5.12/fastutil-8.5.12.jar
curl -L -o lib/classgraph-4.8.165.jar https://repo1.maven.org/maven2/io/github/classgraph/classgraph/4.8.165/classgraph-4.8.165.jar
curl -L -o lib/univocity-parsers-2.9.1.jar https://repo1.maven.org/maven2/com/univocity/univocity-parsers/2.9.1/univocity-parsers-2.9.1.jar
curl -L -o lib/slf4j-api-1.7.36.jar https://repo1.maven.org/maven2/org/slf4j/slf4j-api/1.7.36/slf4j-api-1.7.36.jar
curl -L -o lib/slf4j-simple-1.7.36.jar https://repo1.maven.org/maven2/org/slf4j/slf4j-simple/1.7.36/slf4j-simple-1.7.36.jar
curl -L -o lib/guava-31.1-jre.jar https://repo1.maven.org/maven2/com/google/guava/guava/31.1-jre/guava-31.1-jre.jar
curl -L -o lib/icu4j-72.1.jar https://repo1.maven.org/maven2/com/ibm/icu/icu4j/72.1/icu4j-72.1.jar
curl -L -o lib/commons-math3-3.6.1.jar https://repo1.maven.org/maven2/org/apache/commons/commons-math3/3.6.1/commons-math3-3.6.1.jar
```

Commande de compilation: 

```bash
javac -d bin -sourcepath src -cp "lib/*" src/com/medipass/app/Main.java src/com/medipass/model/*.java src/com/medipass/security/*.java src/com/medipass/service/*.java src/com/medipass/ui/*.java src/com/medipass/user/*.java
```

Commande d'exécution:
```bash
java -cp "bin;lib/*" com.medipass.app.Main
```