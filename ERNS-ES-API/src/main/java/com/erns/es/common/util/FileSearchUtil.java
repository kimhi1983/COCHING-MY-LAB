package com.erns.es.common.util;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 
 * <p>파일검색 유틸리티</p> 
 *
 * @author Hunwoo Park 
 *
 */
public class FileSearchUtil {
	
	/**
	 * 타겟폴더 하위의 모든파일에대해서 탐색한다.
	 * @param targetDir
	 * @param pattern 파일패턴 *, *.TXT, *.txt, *.TxT
	 * @return
	 * @throws IOException
	 */
	public static List<String> findFiles(String targetDir, String pattern) throws IOException {
        List<String> matchedFiles = new ArrayList<>();
        Path startPath = Paths.get(targetDir);

        // Create a case-insensitive regex pattern based on the input glob pattern
        String regexPattern = globToRegex(pattern);
        Pattern compiledPattern = Pattern.compile(regexPattern, Pattern.CASE_INSENSITIVE);

        Files.walkFileTree(startPath, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                if (compiledPattern.matcher(file.getFileName().toString()).matches()) {
                    matchedFiles.add(file.toAbsolutePath().toString());
                }
                return FileVisitResult.CONTINUE;
            }
        });

        return matchedFiles;
    }

    // Converts a glob pattern to a regex pattern for case-insensitive matching
	private static String globToRegex(String glob) {
	    StringBuilder sb = new StringBuilder("(?i)^"); // "(?i)"로 대소문자 무시 설정
	    boolean inGroup = false;
	    for (int i = 0; i < glob.length(); ++i) {
	        char c = glob.charAt(i);
	        switch (c) {
	            case '*':
	                sb.append(".*");
	                break;
	            case '?':
	                sb.append('.');
	                break;
	            case '.':
	                sb.append("\\.");
	                break;
	            case '{':
	                sb.append('(');
	                inGroup = true;
	                break;
	            case '}':
	                sb.append(')');
	                inGroup = false;
	                break;
	            case ',':
	                sb.append(inGroup ? '|' : ',');
	                break;
	            default:
	                sb.append(c);
	        }
	    }
	    sb.append('$');
	    return sb.toString();
	}

    public static void main(String[] args) {
        try {
        	
            String targetDir = "D:\\work\\es_test";
            //String pattern = "*"; // Example pattern (case-insensitive "*.txt")
            String pattern = "*.{xlsX|docX,pptx}"; // Example pattern (case-insensitive "*.txt")
            List<String> files = findFiles(targetDir, pattern);

            files.forEach(System.out::println);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
