/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License;
 * you may not use this file except in compliance with the Elastic License.
 */
package org.elasticsearch.xpack.core.watcher.transport.actions.put;


import org.elasticsearch.action.ActionRequest;
import org.elasticsearch.action.ActionRequestValidationException;
import org.elasticsearch.action.ValidateActions;
import org.elasticsearch.common.bytes.BytesReference;
import org.elasticsearch.common.io.stream.StreamInput;
import org.elasticsearch.common.io.stream.StreamOutput;
import org.elasticsearch.common.lucene.uid.Versions;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.xpack.core.watcher.client.WatchSourceBuilder;
import org.elasticsearch.xpack.core.watcher.support.WatcherUtils;

import java.io.IOException;

/**
 * This request class contains the data needed to create a watch along with the name of the watch.
 * The name of the watch will become the ID of the indexed document.
 */
public class PutWatchRequest extends ActionRequest {

    private String id;
    private BytesReference source;
    private boolean active = true;
    private XContentType xContentType = XContentType.JSON;
    private long version = Versions.MATCH_ANY;

    public PutWatchRequest() {
    }

    public PutWatchRequest(String id, BytesReference source, XContentType xContentType) {
        this.id = id;
        this.source = source;
        this.xContentType = xContentType;
    }

    public PutWatchRequest(StreamInput in) throws IOException {
        super(in);
        id = in.readString();
        source = in.readBytesReference();
        active = in.readBoolean();
        xContentType = in.readEnum(XContentType.class);
        version = in.readZLong();
    }

    @Override
    public void writeTo(StreamOutput out) throws IOException {
        super.writeTo(out);
        out.writeString(id);
        out.writeBytesReference(source);
        out.writeBoolean(active);
        out.writeEnum(xContentType);
        out.writeZLong(version);
    }

    /**
     * @return The name that will be the ID of the indexed document
     */
    public String getId() {
        return id;
    }

    /**
     * Set the watch name
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return The source of the watch
     */
    public BytesReference getSource() {
        return source;
    }

    /**
     * Set the source of the watch
     */
    public void setSource(WatchSourceBuilder source) {
        setSource(source.buildAsBytes(XContentType.JSON), XContentType.JSON);
    }

    /**
     * Set the source of the watch
     */
    public void setSource(BytesReference source, XContentType xContentType) {
        this.source = source;
        this.xContentType = xContentType;
    }

    /**
     * @return The initial active state of the watch (defaults to {@code true}, e.g. "active")
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the initial active state of the watch
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Get the content type for the source
     */
    public XContentType xContentType() {
        return xContentType;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    @Override
    public ActionRequestValidationException validate() {
        ActionRequestValidationException validationException = null;
        if (id == null) {
            validationException = ValidateActions.addValidationError("watch id is missing", validationException);
        } else if (WatcherUtils.isValidId(id) == false) {
            validationException = ValidateActions.addValidationError("watch id contains whitespace", validationException);
        }
        if (source == null) {
            validationException = ValidateActions.addValidationError("watch source is missing", validationException);
        }
        if (xContentType == null) {
            validationException = ValidateActions.addValidationError("request body is missing", validationException);
        }
        return validationException;
    }

    @Override
    public void readFrom(StreamInput in) throws IOException {
        throw new UnsupportedOperationException("usage of Streamable is to be replaced by Writeable");
    }
}
