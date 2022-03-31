package com.obdasystems.swagger.tools;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ImportCode {
    public static void main(String[] args) throws IOException {
        String generatedDir = "/Users/giacomoronconi/Downloads/jaxrs-jersey-server-generated";
        String base = "/Users/giacomoronconi/git/sparqling-ws/src/main/java/com/obdasystems/sparqling";

        String mainApi = generatedDir + "/src/main/java/com/obdasystems/sparqling/api";
        String mainFactories = mainApi + "/factories";
        String mainImpl = mainApi + "/impl";

        String gen = generatedDir + "/src/gen/java/com/obdasystems/sparqling";
        String genApi = gen + "/api";
        String genModel = gen + "/model";

        Set<String> filesNotToCopy = new HashSet<>();
        filesNotToCopy.add("StandaloneApiServiceImpl.java");
        filesNotToCopy.add("StandaloneApi.java");
        filesNotToCopy.add("OntologyGraphApiServiceImpl.java");
        filesNotToCopy.add("QueryGraphBgpApiServiceImpl.java");
        filesNotToCopy.add("QueryGraphFilterApiServiceImpl.java");

        Set<File> api = listFilesUsingJavaIO(mainApi);
        api.addAll(listFilesUsingJavaIO(genApi));
        File apiDir = new File(base + "/api");
        for(File file:api) {
            if(!filesNotToCopy.contains(file.getName())) {
                FileUtils.copyFileToDirectory(file, apiDir);
                file.delete();
            }
        }

        Set<File> factories = listFilesUsingJavaIO(mainFactories);
        File factoriesDir = new File(base + "/api/factories");
        for(File file:factories) {
            FileUtils.copyFileToDirectory(file, factoriesDir);
            file.delete();
        }

        Set<File> impl = listFilesUsingJavaIO(mainImpl);
        File implDir = new File(base + "/api/impl");
        for(File file:impl) {
            if(!filesNotToCopy.contains(file.getName())) {
                FileUtils.copyFileToDirectory(file, implDir);
                file.delete();
            }
        }

        Set<File> model = listFilesUsingJavaIO(genModel);
        File modelDir = new File(base + "/model");
        for(File file:model) {
            FileUtils.copyFileToDirectory(file, modelDir);
            file.delete();
        }
        System.out.println("DONE. REMEMBER TO KEEP CHANGES OF Impl");
    }

    public static Set<File> listFilesUsingJavaIO(String dir) {
        return Stream.of(new File(dir).listFiles())
                .filter(file -> !file.isDirectory())
                .map(File::getAbsoluteFile)
                .collect(Collectors.toSet());
    }
}
