package io.github.fabricators_of_create.porting_lib_build;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

public class Utils {
	public static JsonObject jsonFromPath(Path path) {
		// Use the FileSystem provider directly to avoid Gradle 8.12's NIO interceptor,
		// which incorrectly converts ZIP filesystem paths to host filesystem paths.
		try (InputStream is = path.getFileSystem().provider().newInputStream(path);
			 InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
			return JsonParser.parseReader(reader).getAsJsonObject();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
