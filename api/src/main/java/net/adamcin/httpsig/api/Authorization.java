/*
 * This is free and unencumbered software released into the public domain.
 *
 * Anyone is free to copy, modify, publish, use, compile, sell, or
 * distribute this software, either in source code form or as a compiled
 * binary, for any purpose, commercial or non-commercial, and by any
 * means.
 *
 * In jurisdictions that recognize copyright laws, the author or authors
 * of this software dedicate any and all copyright interest in the
 * software to the public domain. We make this dedication for the benefit
 * of the public at large and to the detriment of our heirs and
 * successors. We intend this dedication to be an overt act of
 * relinquishment in perpetuity of all present and future rights to this
 * software under copyright law.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 *
 * For more information, please refer to <http://unlicense.org/>
 */

package net.adamcin.httpsig.api;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Representation of the "Authorization: Signature..." header sent by the client in response to a {@link Challenge}
 */
public final class Authorization implements Serializable {

    private static final long serialVersionUID = -5642537789425001043L;

    private final String keyId;
    private final String signature;
    private final List<String> headers;
    private final Algorithm algorithm;
    private final Challenge challenge;

    /**
     *
     * @param keyId
     * @param signature
     * @param headers
     * @param algorithm
     */
    public Authorization(final String keyId, final String signature, final List<String> headers, final Algorithm algorithm) {
        this(keyId, signature, headers, algorithm, null);
    }

    /**
     *
     * @param keyId
     * @param signature
     * @param headers
     * @param algorithm
     * @param challenge
     */
    public Authorization(final String keyId, final String signature, final List<String> headers, final Algorithm algorithm, final Challenge challenge) {
        this.keyId = keyId;
        this.signature = signature;
        this.headers = headers != null ? Collections.unmodifiableList(new ArrayList<String>(headers)) : Collections.<String>emptyList();
        this.algorithm = algorithm;
        this.challenge = challenge != null ? challenge : Constants.PREEMPTIVE_CHALLENGE;
    }

    public String getKeyId() {
        return keyId;
    }

    /**
     * @return the signature as a Base64-encoded string
     */
    public String getSignature() {
        return signature;
    }

    /**
     * @return the signature as an unencoded byte array for verification using a {@link Verifier}
     */
    public byte[] getSignatureBytes() {
        return Base64.fromBase64String(this.signature);
    }

    public List<String> getHeaders() {
        return headers;
    }

    public Algorithm getAlgorithm() {
        return algorithm;
    }

    public Challenge getChallenge() {
        return challenge;
    }

    public String getHeaderValue() {
        Map<String, String> params = new LinkedHashMap<String, String>();
        params.put(Constants.KEY_ID, keyId);
        params.put(Constants.SIGNATURE, signature);
        params.put(Constants.HEADERS, Constants.constructTokensString(getHeaders()));
        params.put(Constants.ALGORITHM, algorithm.getName());
        return Constants.constructRFC2617(params);
    }

    @Override
    public String toString() {
        return getHeaderValue();
    }

    public static Authorization parse(String header) {
        if (header == null) {
            return null;
        }

        Map<String, String> params = Constants.parseRFC2617(header);

        if (params.containsKey(Constants.KEY_ID)
                && params.containsKey(Constants.HEADERS)
                && params.containsKey(Constants.SIGNATURE)
                && params.containsKey(Constants.ALGORITHM)) {

            String keyId = params.get(Constants.KEY_ID);
            String signature = params.get(Constants.SIGNATURE);
            String headers = params.get(Constants.HEADERS);
            String algorithm = params.get(Constants.ALGORITHM);

            return new Authorization(keyId, signature, Constants.parseTokens(headers), Algorithm.forName(algorithm));
        } else {
            return null;
        }
    }
}