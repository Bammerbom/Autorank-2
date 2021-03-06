package me.armar.plugins.autorank.util.uuid;

import com.google.common.collect.ImmutableList;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.Callable;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * This class is used to get the UUID of a certain player.
 * <p>
 * Date created: 17:00:41 2 apr. 2014
 *
 * @author evilmidget38
 *
 */
public class UUIDFetcher implements Callable<Map<String, UUID>> {

    private static final String PROFILE_URL = "https://api.mojang.com/profiles/minecraft";
    private static final double PROFILES_PER_REQUEST = 100;

    private static HttpURLConnection createConnection() throws Exception {
        final URL url = new URL(PROFILE_URL);
        final HttpURLConnection connection = (HttpURLConnection) url
                .openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setUseCaches(false);
        connection.setDoInput(true);
        connection.setDoOutput(true);
        return connection;
    }

    public static UUID fromBytes(final byte[] array) {
        if (array.length != 16) {
            throw new IllegalArgumentException("Illegal byte array length: "
                    + array.length);
        }
        final ByteBuffer byteBuffer = ByteBuffer.wrap(array);
        final long mostSignificant = byteBuffer.getLong();
        final long leastSignificant = byteBuffer.getLong();
        return new UUID(mostSignificant, leastSignificant);
    }

    private static UUID getUUID(final String id) {
        return UUID.fromString(id.substring(0, 8) + "-" + id.substring(8, 12)
                + "-" + id.substring(12, 16) + "-" + id.substring(16, 20) + "-"
                + id.substring(20, 32));
    }

    public static UUID getUUIDOf(final String name) throws Exception {
        return new UUIDFetcher(Arrays.asList(name)).call().get(name);
    }

    public static byte[] toBytes(final UUID uuid) {
        final ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[16]);
        byteBuffer.putLong(uuid.getMostSignificantBits());
        byteBuffer.putLong(uuid.getLeastSignificantBits());
        return byteBuffer.array();
    }

    private static void writeBody(final HttpURLConnection connection,
            final String body) throws Exception {
        final OutputStream stream = connection.getOutputStream();
        stream.write(body.getBytes());
        stream.flush();
        stream.close();
    }

    private final JSONParser jsonParser = new JSONParser();

    private final List<String> names;

    private final boolean rateLimiting;

    public UUIDFetcher(final List<String> names) {
        this(names, true);
    }

    public UUIDFetcher(final List<String> names, final boolean rateLimiting) {
        this.names = ImmutableList.copyOf(names);
        this.rateLimiting = rateLimiting;
    }

    @Override
    public Map<String, UUID> call() throws Exception {
        final Map<String, UUID> uuidMap = new HashMap<String, UUID>();
        final int requests = (int) Math.ceil(names.size()
                / PROFILES_PER_REQUEST);
        for (int i = 0; i < requests; i++) {
            final HttpURLConnection connection = createConnection();
            final String body = JSONArray.toJSONString(names.subList(i * 100,
                    Math.min((i + 1) * 100, names.size())));
            writeBody(connection, body);
            JSONArray array;

            try {
                array = (JSONArray) jsonParser.parse(new InputStreamReader(
                        connection.getInputStream()));
            } catch (final Exception e) {
                System.out.print("[Autorank] Could not fetch UUID of player '"
                        + names.get(i) + "'!");
                continue;
            }

            for (final Object profile : array) {
                final JSONObject jsonProfile = (JSONObject) profile;
                final String id = (String) jsonProfile.get("id");
                final String name = (String) jsonProfile.get("name");

                final UUID uuid = UUIDFetcher.getUUID(id);

                uuidMap.put(name, uuid);
            }
            if (rateLimiting && i != requests - 1) {
                Thread.sleep(100L);
            }
        }
        return uuidMap;
    }
}
