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

package net.adamcin.httpsig.http.ning;

import com.ning.http.client.Request;
import com.ning.http.client.RequestBuilderBase;
import com.ning.http.client.SignatureCalculator;
import net.adamcin.httpsig.api.Signer;

/**
 * Implementation of {@link SignatureCalculator} using a {@link Signer}
 */
public class AsyncSignatureCalculator implements SignatureCalculator {
    private static final SignatureCalculator DEFAULT_DELEGATEE = new SignatureCalculator() {
        public void calculateAndAddSignature(Request request, RequestBuilderBase<?> requestBuilder) { }
    };

    private final Signer signer;
    private final SignatureCalculator delegatee;

    public AsyncSignatureCalculator(Signer signer) {
        this(signer, null);
    }

    public AsyncSignatureCalculator(Signer signer, SignatureCalculator delegatee) {
        this.signer = signer;
        this.delegatee = delegatee != null ? delegatee : DEFAULT_DELEGATEE;
    }

    /**
     * {@inheritDoc}
     */
    public void calculateAndAddSignature(Request request, RequestBuilderBase<?> requestBuilder) {
        delegatee.calculateAndAddSignature(request, requestBuilder);
        AsyncUtil.calculateSignature(this.signer, request, requestBuilder);
    }
}
