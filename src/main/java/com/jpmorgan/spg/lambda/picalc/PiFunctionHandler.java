package com.jpmorgan.spg.lambda.picalc;

import java.lang.management.ManagementFactory;
import org.apache.commons.lang3.tuple.Pair;
import java.time.Duration;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.amazonaws.services.lambda.runtime.ClientContext;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;


public class PiFunctionHandler implements RequestHandler<HttpQuerystringRequest, HttpProductResponse> {
	private static final int DEFAULT_PATH=1000;
	private static final int DEFAULT_RANDOM_TO_SAVE=1000;
	private static boolean firstRun = true;

    @Override
    public HttpProductResponse handleRequest(HttpQuerystringRequest request, Context context) {
        context.getLogger().log("Input: " + request + ", firstRun = " + firstRun);   
    	Boolean coldStart = (firstRun)? true : false;
    	firstRun = false;
    	
        logMemInfo();
    	printEnv(context);  
      
        Pair<Integer, Integer> pair = parseInputParameters(request, context);
        
        int path = pair.getKey();
        int randomToSave = pair.getValue();
        
        context.getLogger().log("parameter: path =  " + path 
            + ", number of randoms to save = " + randomToSave);   
    	
        Instant start = Instant.now();
        
        double pi = calcPi(path, randomToSave);
        
        Instant finish = Instant.now();
        
        long timeElapsed = Duration.between(start, finish).toMillis();
        
        PiCalcResult piCalcResult = new PiCalcResult(context.getMemoryLimitInMB(), path, randomToSave, timeElapsed, coldStart, pi);

        logProcessInfo();
        logMemInfo();

        return new HttpProductResponse(piCalcResult);
    }


    private Pair<Integer, Integer> parseInputParameters(HttpQuerystringRequest request, Context context) {
        int path = DEFAULT_PATH;
        int randomCache = DEFAULT_RANDOM_TO_SAVE;
        
        Map<String, String> params = request.getQueryStringParameters();
        
        if (params == null) {
        	context.getLogger().log("request params is null");
        }
        else {

			String pathAsString = (String) params.get("path");

			if (pathAsString != null) {
				try {
					path = Integer.parseInt(pathAsString);
				} catch (NumberFormatException ex) {
					context.getLogger().log("path is not number, default used. " + DEFAULT_PATH);
				}
			}

			String tmpMemSizeAsString = (String) params.get("random-to-save");

			if (tmpMemSizeAsString != null) {
				try {
					randomCache = Integer.parseInt(tmpMemSizeAsString);
				} catch (NumberFormatException ex) {
					context.getLogger()
							.log("random-number-to-save is not number, default used. " + DEFAULT_RANDOM_TO_SAVE);
				}
			}
		}
        
        return Pair.of(path, randomCache);
    }
    
	private void logProcessInfo() {
		// TODO Auto-generated method stub
		System.out.println(ManagementFactory.getRuntimeMXBean().getName());

	}
	
	private void logMemInfo() {
		Runtime runtime = Runtime.getRuntime();
		long totalMem = runtime.totalMemory();
		long maxMem = runtime.maxMemory();
		long freeMem = runtime.freeMemory();
		System.out.println("max mem = " + maxMem + ", total Mem = " + totalMem + ", free mem = " + freeMem +
				". used mem = " + (totalMem - freeMem) );
	}


	private void printEnv(Context context) {
		ClientContext clientContext = context.getClientContext();
    	if (clientContext == null) {
    		context.getLogger().log("client context is null");
    	}
    	else {
    		 Map<String, String> env = clientContext.getEnvironment();
    		 logEnv(env);
    	}
    	
    	// log system env
    	context.getLogger().log("logging system env");
    	Map<String, String> sysEnv = System.getenv();
    	if (sysEnv == null) {
    		context.getLogger().log("system env is null");
    	}
    	else {
            logEnv(sysEnv);
    	}
	}

        
    private void logEnv(Map<String, String> env) {
        LinkedHashMap<String, String> collect =
        		env.entrySet().stream()
        		.sorted(Map.Entry.comparingByKey())
        		.collect(Collectors.toMap(Map.Entry::getKey,  Map.Entry::getValue,
        				(oldValue, newVlaue)->oldValue, LinkedHashMap::new));
        
        collect.forEach((k,v) -> System.out.println(k + ":" +  v));
	}



	private double calcPi(double path, int cacheSize) {
    	int i;
    	int nThrows = 0;
    	int nSuccess = 0;
    	
    	double x, y;
    	int indexX;
    	int indexY;

    	if (cacheSize < 2) {
    		// set to 2 by default to hold at least 2 random numbers
    		cacheSize = 2;
    	}
    	
    	double[] randoms = new double[cacheSize];
    	int randomSaved = 0;
    	
    	for (i = 0; i < path; i++) {
    		x = Math.random();
    		y = Math.random();
    		
    		if (randomSaved >= cacheSize) {
    			randomSaved = 0; // rewind to the beginning of array        			
    		}
    		
  			indexX = randomSaved;  			
    		randoms[randomSaved++] = Math.random();

    		indexY = randomSaved;
    		randoms[randomSaved++] = Math.random();
    	
    		x = randoms[indexX];
    		y = randoms[indexY];
    		
    		nThrows ++;

    		if ( x*x + y*y <= 1) {
    			nSuccess++;
    		}
    	}
    	
    	return 4*(double)nSuccess/(double)nThrows;
    	
    }

}
