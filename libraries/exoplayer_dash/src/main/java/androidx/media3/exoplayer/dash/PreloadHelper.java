package androidx.media3.exoplayer.dash;

import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.media3.common.C;
import androidx.media3.datasource.DataSpec;
import androidx.media3.datasource.cache.Cache;
import androidx.media3.datasource.cache.CacheKeyFactory;
import androidx.media3.datasource.cache.ContentMetadata;
import androidx.media3.exoplayer.dash.manifest.RangedUri;
import androidx.media3.exoplayer.dash.manifest.Representation;

public class PreloadHelper {

  public static DataSpec buildInitDataSpec(
      @NonNull Representation representation,
      @Nullable RangedUri initializationUri,
      @Nullable RangedUri indexUri) {
      RangedUri requestUri;
    if (initializationUri != null) {
      requestUri = initializationUri.attemptMerge(indexUri, representation.baseUrls.get(0).url);
      if (requestUri == null) {
        requestUri = initializationUri;
      }
    } else {
      requestUri = indexUri;
    }
    return DashUtil.buildDataSpec(representation, requestUri, 0);
  }

  public static boolean isDataSpecPreloaded(Cache cache, CacheKeyFactory keyFactory, DataSpec dataSpec) {
    String cacheKey = keyFactory.buildCacheKey(dataSpec);
    ContentMetadata meta = cache.getContentMetadata(cacheKey);
    long preloadedLength = ContentMetadata.getContentLength(meta);
    boolean isCached = cache.isCached(cacheKey, dataSpec.position, dataSpec.length);

    long cachedLength = cache.getCachedLength(cacheKey, 0, preloadedLength);
    long cachedBytes = cache.getCachedBytes(cacheKey, 0, preloadedLength);

    Log.d("PreloadHelper","isCached=" + isCached + " preloadedLength=" + preloadedLength + " cachedLength=" + cachedLength +
          " cachedBytes=" + cachedBytes + " dataSpec=" + dataSpec + " cacheKey=" + cacheKey + " meta=" + meta);

    boolean isDataSpecPreloaded = preloadedLength != C.LENGTH_UNSET || isCached;
    Log.d("PreloadHelper","isDataSpecPreloaded ==" + isDataSpecPreloaded);
    return isDataSpecPreloaded;
  }

  private static String printRepresentation(Representation representation) {
    return "Representation: height=" + representation.format.height;
  }
}