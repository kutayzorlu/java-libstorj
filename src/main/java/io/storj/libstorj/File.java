/*
 * Copyright (C) 2017-2018 Kaloyan Raev
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package io.storj.libstorj;

import java.net.URLConnection;
import java.util.Objects;

/**
 * A class representing a file in the Storj Bridge.
 */
@SuppressWarnings("serial")
public class File extends Entry {

    private String bucketId;
    private long size;
    private String mimeType;
    private String erasure;
    private String index;
    private String hmac;

    /**
     * Constructs new File object with the provided metadata.
     * 
     * @param id
     *            the file id
     * @param bucketId
     *            the id of the bucket containing the file
     * @param name
     *            the file name
     * @param created
     *            the formatted UTC time when the file was uploaded
     * @param decrypted
     *            if the file name is decrypted
     * @param size
     *            the file size in bytes
     * @param mimeType
     *            the file's content type
     * @param erasure
     *            the erasure encoding algorithm used for this file
     * @param index
     *            the index that the encryption key for this file is derived from
     * @param hmac
     *            an HMAC checksum of the file
     */
    public File(String id,
                String bucketId,
                String name,
                String created,
                boolean decrypted,
                long size,
                String mimeType,
                String erasure,
                String index,
                String hmac) {
        super(id, name, created, decrypted);
        this.bucketId = bucketId;
        this.size = size;
        this.mimeType = mimeType;
        this.erasure = erasure;
        this.index = index;
        this.hmac = hmac;
    }

    /**
     * Returns the id of the bucket id containing this file.
     * 
     * @return a bucket id
     */
    public String getBucketId() {
        return bucketId;
    }

    /**
     * Returns the file size in bytes.
     * 
     * @return the number of bytes
     */
    public long getSize() {
        return size;
    }

    /**
     * Returns the content type of the file.
     * 
     * <p>
     * Currently the libstorj native library does not determine the file's content
     * type during upload, so most probably the Storj Bridge stores it as
     * 'application/octet-stream' in the file metadata. Thus, an attempt is made to
     * determine the content type by calling
     * {@link URLConnection#guessContentTypeFromName(String)}
     * </p>
     * 
     * @return a guess of the content type based on the file name
     */
    public String getMimeType() {
        // prefer the Java util as libstorj returns always 'application/octet-stream'
        String mime = URLConnection.guessContentTypeFromName(name);
        if (mime == null || mime.isEmpty()) {
            // fallback to libstorj
            mime = mimeType;
        }
        return mime;
    }

    /**
     * Returns the erasure encoding algorithm used for this file.
     * 
     * @return the name of the erasure encoding algorithm
     */
    public String getErasure() {
        return erasure;
    }

    /**
     * Returns the index that the encryption key for this file is derived from.
     * 
     * @return the 32 bit hex value of the index
     */
    public String getIndex() {
        return index;
    }

    /**
     * Returns the HMAC checksum for the file.
     * 
     * <p>
     * The checksum is used for verifying the file's integrity and authenticity. The
     * formula is described in <a href=
     * "https://github.com/Storj/sips/blob/master/sip-0005.md#data-integrity-and-authenticity">SIP5</a>.
     * </p>
     * 
     * @return the HMAC checksum
     */
    public String getHMAC() {
        return hmac;
    }

    /**
     * Checks if the file is a directory.
     * 
     * <p>
     * The Storj Bridge has a flat structure of bucket and files. There is no real
     * file tree structure with subdirectories. However, a pseudo tree structure can
     * be maintained by clients using file paths as the file name, e.g.
     * "mydir/mysubdir/myfile". It is assumed that a file is a directory if its name
     * ends with a slash character, e.g. "mydir/mysubdir/".
     * </p>
     * 
     * @return <code>true</code> if this file object is a directory,
     *         <code>false</code> otherwise
     */
    public boolean isDirectory() {
        return name.endsWith("/");
    }

    /**
     * The hash code value of the File object is the hash code value of its id.
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * Two File objects are equal if their ids are equal.
     */
    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof File)) {
            return false;
        }
        File file = (File) o;
        return Objects.equals(id, file.id);
    }

}
