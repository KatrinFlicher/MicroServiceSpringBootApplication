package by.training.katrinflicher;

import java.lang.reflect.Proxy;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;

public class MetaSpaceOOMGenerator {
        public static void generateError(){
            Map<String, Comparable> myMap = new HashMap<>();
            int iterations=0;
            try{
                while(true){
                    String classLoaderJAR = "file:" +iterations+".jar";
                    URL[] urlsOfJar = new URL[] {new URL(classLoaderJAR)};
                    URLClassLoader aUrlClassLoader = new URLClassLoader(urlsOfJar);
                    Comparable proxyObj = (Comparable) Proxy.newProxyInstance(aUrlClassLoader,
                            new Class<?>[]{Comparable.class},
                            (proxy,method,args)->{return method.invoke(proxy,args);});
                    System.out.println(proxyObj.getClass());
                    myMap.put(classLoaderJAR, proxyObj);
                    iterations++;
                }
            }catch(Throwable anyError){
                anyError.printStackTrace();
            }
        }
}
