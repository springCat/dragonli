package org.springcat.dragonli.client;

import java.io.IOException;
import java.util.Map;

public interface IHttpTransform {
      String post(String url, String data, Map<String, String> headers);
}
