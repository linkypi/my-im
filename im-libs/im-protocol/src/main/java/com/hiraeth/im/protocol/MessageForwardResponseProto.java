// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: MessageForwardResponse.proto

// Protobuf Java Version: 3.25.1
package com.hiraeth.im.protocol;

public final class MessageForwardResponseProto {
  private MessageForwardResponseProto() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  public interface MessageForwardResponseOrBuilder extends
      // @@protoc_insertion_point(interface_extends:im.MessageForwardResponse)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <code>bool success = 1;</code>
     * @return The success.
     */
    boolean getSuccess();

    /**
     * <pre>
     * 错误码
     * </pre>
     *
     * <code>.im.StatusCode code = 2;</code>
     * @return The enum numeric value on the wire for code.
     */
    int getCodeValue();
    /**
     * <pre>
     * 错误码
     * </pre>
     *
     * <code>.im.StatusCode code = 2;</code>
     * @return The code.
     */
    StatusCodeEnum.StatusCode getCode();

    /**
     * <pre>
     * 提示信息
     * </pre>
     *
     * <code>string message = 3;</code>
     * @return The message.
     */
    String getMessage();
    /**
     * <pre>
     * 提示信息
     * </pre>
     *
     * <code>string message = 3;</code>
     * @return The bytes for message.
     */
    com.google.protobuf.ByteString
        getMessageBytes();

    /**
     * <code>string sender_id = 4;</code>
     * @return The senderId.
     */
    String getSenderId();
    /**
     * <code>string sender_id = 4;</code>
     * @return The bytes for senderId.
     */
    com.google.protobuf.ByteString
        getSenderIdBytes();

    /**
     * <code>string receiver_id = 5;</code>
     * @return The receiverId.
     */
    String getReceiverId();
    /**
     * <code>string receiver_id = 5;</code>
     * @return The bytes for receiverId.
     */
    com.google.protobuf.ByteString
        getReceiverIdBytes();

    /**
     * <pre>
     * 消息id
     * </pre>
     *
     * <code>int64 message_id = 6;</code>
     * @return The messageId.
     */
    long getMessageId();
  }
  /**
   * Protobuf type {@code im.MessageForwardResponse}
   */
  public static final class MessageForwardResponse extends
      com.google.protobuf.GeneratedMessageV3 implements
      // @@protoc_insertion_point(message_implements:im.MessageForwardResponse)
      MessageForwardResponseOrBuilder {
  private static final long serialVersionUID = 0L;
    // Use MessageForwardResponse.newBuilder() to construct.
    private MessageForwardResponse(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
      super(builder);
    }
    private MessageForwardResponse() {
      code_ = 0;
      message_ = "";
      senderId_ = "";
      receiverId_ = "";
    }

    @Override
    @SuppressWarnings({"unused"})
    protected Object newInstance(
        UnusedPrivateParameter unused) {
      return new MessageForwardResponse();
    }

    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return MessageForwardResponseProto.internal_static_im_MessageForwardResponse_descriptor;
    }

    @Override
    protected FieldAccessorTable
        internalGetFieldAccessorTable() {
      return MessageForwardResponseProto.internal_static_im_MessageForwardResponse_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              MessageForwardResponse.class, Builder.class);
    }

    public static final int SUCCESS_FIELD_NUMBER = 1;
    private boolean success_ = false;
    /**
     * <code>bool success = 1;</code>
     * @return The success.
     */
    @Override
    public boolean getSuccess() {
      return success_;
    }

    public static final int CODE_FIELD_NUMBER = 2;
    private int code_ = 0;
    /**
     * <pre>
     * 错误码
     * </pre>
     *
     * <code>.im.StatusCode code = 2;</code>
     * @return The enum numeric value on the wire for code.
     */
    @Override public int getCodeValue() {
      return code_;
    }
    /**
     * <pre>
     * 错误码
     * </pre>
     *
     * <code>.im.StatusCode code = 2;</code>
     * @return The code.
     */
    @Override public StatusCodeEnum.StatusCode getCode() {
      StatusCodeEnum.StatusCode result = StatusCodeEnum.StatusCode.forNumber(code_);
      return result == null ? StatusCodeEnum.StatusCode.UNRECOGNIZED : result;
    }

    public static final int MESSAGE_FIELD_NUMBER = 3;
    @SuppressWarnings("serial")
    private volatile Object message_ = "";
    /**
     * <pre>
     * 提示信息
     * </pre>
     *
     * <code>string message = 3;</code>
     * @return The message.
     */
    @Override
    public String getMessage() {
      Object ref = message_;
      if (ref instanceof String) {
        return (String) ref;
      } else {
        com.google.protobuf.ByteString bs = 
            (com.google.protobuf.ByteString) ref;
        String s = bs.toStringUtf8();
        message_ = s;
        return s;
      }
    }
    /**
     * <pre>
     * 提示信息
     * </pre>
     *
     * <code>string message = 3;</code>
     * @return The bytes for message.
     */
    @Override
    public com.google.protobuf.ByteString
        getMessageBytes() {
      Object ref = message_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (String) ref);
        message_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }

    public static final int SENDER_ID_FIELD_NUMBER = 4;
    @SuppressWarnings("serial")
    private volatile Object senderId_ = "";
    /**
     * <code>string sender_id = 4;</code>
     * @return The senderId.
     */
    @Override
    public String getSenderId() {
      Object ref = senderId_;
      if (ref instanceof String) {
        return (String) ref;
      } else {
        com.google.protobuf.ByteString bs = 
            (com.google.protobuf.ByteString) ref;
        String s = bs.toStringUtf8();
        senderId_ = s;
        return s;
      }
    }
    /**
     * <code>string sender_id = 4;</code>
     * @return The bytes for senderId.
     */
    @Override
    public com.google.protobuf.ByteString
        getSenderIdBytes() {
      Object ref = senderId_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (String) ref);
        senderId_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }

    public static final int RECEIVER_ID_FIELD_NUMBER = 5;
    @SuppressWarnings("serial")
    private volatile Object receiverId_ = "";
    /**
     * <code>string receiver_id = 5;</code>
     * @return The receiverId.
     */
    @Override
    public String getReceiverId() {
      Object ref = receiverId_;
      if (ref instanceof String) {
        return (String) ref;
      } else {
        com.google.protobuf.ByteString bs = 
            (com.google.protobuf.ByteString) ref;
        String s = bs.toStringUtf8();
        receiverId_ = s;
        return s;
      }
    }
    /**
     * <code>string receiver_id = 5;</code>
     * @return The bytes for receiverId.
     */
    @Override
    public com.google.protobuf.ByteString
        getReceiverIdBytes() {
      Object ref = receiverId_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (String) ref);
        receiverId_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }

    public static final int MESSAGE_ID_FIELD_NUMBER = 6;
    private long messageId_ = 0L;
    /**
     * <pre>
     * 消息id
     * </pre>
     *
     * <code>int64 message_id = 6;</code>
     * @return The messageId.
     */
    @Override
    public long getMessageId() {
      return messageId_;
    }

    private byte memoizedIsInitialized = -1;
    @Override
    public final boolean isInitialized() {
      byte isInitialized = memoizedIsInitialized;
      if (isInitialized == 1) return true;
      if (isInitialized == 0) return false;

      memoizedIsInitialized = 1;
      return true;
    }

    @Override
    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      if (success_ != false) {
        output.writeBool(1, success_);
      }
      if (code_ != StatusCodeEnum.StatusCode.SUCCESS.getNumber()) {
        output.writeEnum(2, code_);
      }
      if (!com.google.protobuf.GeneratedMessageV3.isStringEmpty(message_)) {
        com.google.protobuf.GeneratedMessageV3.writeString(output, 3, message_);
      }
      if (!com.google.protobuf.GeneratedMessageV3.isStringEmpty(senderId_)) {
        com.google.protobuf.GeneratedMessageV3.writeString(output, 4, senderId_);
      }
      if (!com.google.protobuf.GeneratedMessageV3.isStringEmpty(receiverId_)) {
        com.google.protobuf.GeneratedMessageV3.writeString(output, 5, receiverId_);
      }
      if (messageId_ != 0L) {
        output.writeInt64(6, messageId_);
      }
      getUnknownFields().writeTo(output);
    }

    @Override
    public int getSerializedSize() {
      int size = memoizedSize;
      if (size != -1) return size;

      size = 0;
      if (success_ != false) {
        size += com.google.protobuf.CodedOutputStream
          .computeBoolSize(1, success_);
      }
      if (code_ != StatusCodeEnum.StatusCode.SUCCESS.getNumber()) {
        size += com.google.protobuf.CodedOutputStream
          .computeEnumSize(2, code_);
      }
      if (!com.google.protobuf.GeneratedMessageV3.isStringEmpty(message_)) {
        size += com.google.protobuf.GeneratedMessageV3.computeStringSize(3, message_);
      }
      if (!com.google.protobuf.GeneratedMessageV3.isStringEmpty(senderId_)) {
        size += com.google.protobuf.GeneratedMessageV3.computeStringSize(4, senderId_);
      }
      if (!com.google.protobuf.GeneratedMessageV3.isStringEmpty(receiverId_)) {
        size += com.google.protobuf.GeneratedMessageV3.computeStringSize(5, receiverId_);
      }
      if (messageId_ != 0L) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt64Size(6, messageId_);
      }
      size += getUnknownFields().getSerializedSize();
      memoizedSize = size;
      return size;
    }

    @Override
    public boolean equals(final Object obj) {
      if (obj == this) {
       return true;
      }
      if (!(obj instanceof MessageForwardResponse)) {
        return super.equals(obj);
      }
      MessageForwardResponse other = (MessageForwardResponse) obj;

      if (getSuccess()
          != other.getSuccess()) return false;
      if (code_ != other.code_) return false;
      if (!getMessage()
          .equals(other.getMessage())) return false;
      if (!getSenderId()
          .equals(other.getSenderId())) return false;
      if (!getReceiverId()
          .equals(other.getReceiverId())) return false;
      if (getMessageId()
          != other.getMessageId()) return false;
      if (!getUnknownFields().equals(other.getUnknownFields())) return false;
      return true;
    }

    @Override
    public int hashCode() {
      if (memoizedHashCode != 0) {
        return memoizedHashCode;
      }
      int hash = 41;
      hash = (19 * hash) + getDescriptor().hashCode();
      hash = (37 * hash) + SUCCESS_FIELD_NUMBER;
      hash = (53 * hash) + com.google.protobuf.Internal.hashBoolean(
          getSuccess());
      hash = (37 * hash) + CODE_FIELD_NUMBER;
      hash = (53 * hash) + code_;
      hash = (37 * hash) + MESSAGE_FIELD_NUMBER;
      hash = (53 * hash) + getMessage().hashCode();
      hash = (37 * hash) + SENDER_ID_FIELD_NUMBER;
      hash = (53 * hash) + getSenderId().hashCode();
      hash = (37 * hash) + RECEIVER_ID_FIELD_NUMBER;
      hash = (53 * hash) + getReceiverId().hashCode();
      hash = (37 * hash) + MESSAGE_ID_FIELD_NUMBER;
      hash = (53 * hash) + com.google.protobuf.Internal.hashLong(
          getMessageId());
      hash = (29 * hash) + getUnknownFields().hashCode();
      memoizedHashCode = hash;
      return hash;
    }

    public static MessageForwardResponse parseFrom(
        java.nio.ByteBuffer data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static MessageForwardResponse parseFrom(
        java.nio.ByteBuffer data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static MessageForwardResponse parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static MessageForwardResponse parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static MessageForwardResponse parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static MessageForwardResponse parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static MessageForwardResponse parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static MessageForwardResponse parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }

    public static MessageForwardResponse parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input);
    }

    public static MessageForwardResponse parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    public static MessageForwardResponse parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static MessageForwardResponse parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }

    @Override
    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
    }
    public static Builder newBuilder(MessageForwardResponse prototype) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
    }
    @Override
    public Builder toBuilder() {
      return this == DEFAULT_INSTANCE
          ? new Builder() : new Builder().mergeFrom(this);
    }

    @Override
    protected Builder newBuilderForType(
        BuilderParent parent) {
      Builder builder = new Builder(parent);
      return builder;
    }
    /**
     * Protobuf type {@code im.MessageForwardResponse}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:im.MessageForwardResponse)
        MessageForwardResponseOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return MessageForwardResponseProto.internal_static_im_MessageForwardResponse_descriptor;
      }

      @Override
      protected FieldAccessorTable
          internalGetFieldAccessorTable() {
        return MessageForwardResponseProto.internal_static_im_MessageForwardResponse_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                MessageForwardResponse.class, Builder.class);
      }

      // Construct using com.hiraeth.im.protocol.MessageForwardResponseProto.MessageForwardResponse.newBuilder()
      private Builder() {

      }

      private Builder(
          BuilderParent parent) {
        super(parent);

      }
      @Override
      public Builder clear() {
        super.clear();
        bitField0_ = 0;
        success_ = false;
        code_ = 0;
        message_ = "";
        senderId_ = "";
        receiverId_ = "";
        messageId_ = 0L;
        return this;
      }

      @Override
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return MessageForwardResponseProto.internal_static_im_MessageForwardResponse_descriptor;
      }

      @Override
      public MessageForwardResponse getDefaultInstanceForType() {
        return MessageForwardResponse.getDefaultInstance();
      }

      @Override
      public MessageForwardResponse build() {
        MessageForwardResponse result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      @Override
      public MessageForwardResponse buildPartial() {
        MessageForwardResponse result = new MessageForwardResponse(this);
        if (bitField0_ != 0) { buildPartial0(result); }
        onBuilt();
        return result;
      }

      private void buildPartial0(MessageForwardResponse result) {
        int from_bitField0_ = bitField0_;
        if (((from_bitField0_ & 0x00000001) != 0)) {
          result.success_ = success_;
        }
        if (((from_bitField0_ & 0x00000002) != 0)) {
          result.code_ = code_;
        }
        if (((from_bitField0_ & 0x00000004) != 0)) {
          result.message_ = message_;
        }
        if (((from_bitField0_ & 0x00000008) != 0)) {
          result.senderId_ = senderId_;
        }
        if (((from_bitField0_ & 0x00000010) != 0)) {
          result.receiverId_ = receiverId_;
        }
        if (((from_bitField0_ & 0x00000020) != 0)) {
          result.messageId_ = messageId_;
        }
      }

      @Override
      public Builder clone() {
        return super.clone();
      }
      @Override
      public Builder setField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          Object value) {
        return super.setField(field, value);
      }
      @Override
      public Builder clearField(
          com.google.protobuf.Descriptors.FieldDescriptor field) {
        return super.clearField(field);
      }
      @Override
      public Builder clearOneof(
          com.google.protobuf.Descriptors.OneofDescriptor oneof) {
        return super.clearOneof(oneof);
      }
      @Override
      public Builder setRepeatedField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          int index, Object value) {
        return super.setRepeatedField(field, index, value);
      }
      @Override
      public Builder addRepeatedField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          Object value) {
        return super.addRepeatedField(field, value);
      }
      @Override
      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof MessageForwardResponse) {
          return mergeFrom((MessageForwardResponse)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(MessageForwardResponse other) {
        if (other == MessageForwardResponse.getDefaultInstance()) return this;
        if (other.getSuccess() != false) {
          setSuccess(other.getSuccess());
        }
        if (other.code_ != 0) {
          setCodeValue(other.getCodeValue());
        }
        if (!other.getMessage().isEmpty()) {
          message_ = other.message_;
          bitField0_ |= 0x00000004;
          onChanged();
        }
        if (!other.getSenderId().isEmpty()) {
          senderId_ = other.senderId_;
          bitField0_ |= 0x00000008;
          onChanged();
        }
        if (!other.getReceiverId().isEmpty()) {
          receiverId_ = other.receiverId_;
          bitField0_ |= 0x00000010;
          onChanged();
        }
        if (other.getMessageId() != 0L) {
          setMessageId(other.getMessageId());
        }
        this.mergeUnknownFields(other.getUnknownFields());
        onChanged();
        return this;
      }

      @Override
      public final boolean isInitialized() {
        return true;
      }

      @Override
      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        if (extensionRegistry == null) {
          throw new NullPointerException();
        }
        try {
          boolean done = false;
          while (!done) {
            int tag = input.readTag();
            switch (tag) {
              case 0:
                done = true;
                break;
              case 8: {
                success_ = input.readBool();
                bitField0_ |= 0x00000001;
                break;
              } // case 8
              case 16: {
                code_ = input.readEnum();
                bitField0_ |= 0x00000002;
                break;
              } // case 16
              case 26: {
                message_ = input.readStringRequireUtf8();
                bitField0_ |= 0x00000004;
                break;
              } // case 26
              case 34: {
                senderId_ = input.readStringRequireUtf8();
                bitField0_ |= 0x00000008;
                break;
              } // case 34
              case 42: {
                receiverId_ = input.readStringRequireUtf8();
                bitField0_ |= 0x00000010;
                break;
              } // case 42
              case 48: {
                messageId_ = input.readInt64();
                bitField0_ |= 0x00000020;
                break;
              } // case 48
              default: {
                if (!super.parseUnknownField(input, extensionRegistry, tag)) {
                  done = true; // was an endgroup tag
                }
                break;
              } // default:
            } // switch (tag)
          } // while (!done)
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          throw e.unwrapIOException();
        } finally {
          onChanged();
        } // finally
        return this;
      }
      private int bitField0_;

      private boolean success_ ;
      /**
       * <code>bool success = 1;</code>
       * @return The success.
       */
      @Override
      public boolean getSuccess() {
        return success_;
      }
      /**
       * <code>bool success = 1;</code>
       * @param value The success to set.
       * @return This builder for chaining.
       */
      public Builder setSuccess(boolean value) {

        success_ = value;
        bitField0_ |= 0x00000001;
        onChanged();
        return this;
      }
      /**
       * <code>bool success = 1;</code>
       * @return This builder for chaining.
       */
      public Builder clearSuccess() {
        bitField0_ = (bitField0_ & ~0x00000001);
        success_ = false;
        onChanged();
        return this;
      }

      private int code_ = 0;
      /**
       * <pre>
       * 错误码
       * </pre>
       *
       * <code>.im.StatusCode code = 2;</code>
       * @return The enum numeric value on the wire for code.
       */
      @Override public int getCodeValue() {
        return code_;
      }
      /**
       * <pre>
       * 错误码
       * </pre>
       *
       * <code>.im.StatusCode code = 2;</code>
       * @param value The enum numeric value on the wire for code to set.
       * @return This builder for chaining.
       */
      public Builder setCodeValue(int value) {
        code_ = value;
        bitField0_ |= 0x00000002;
        onChanged();
        return this;
      }
      /**
       * <pre>
       * 错误码
       * </pre>
       *
       * <code>.im.StatusCode code = 2;</code>
       * @return The code.
       */
      @Override
      public StatusCodeEnum.StatusCode getCode() {
        StatusCodeEnum.StatusCode result = StatusCodeEnum.StatusCode.forNumber(code_);
        return result == null ? StatusCodeEnum.StatusCode.UNRECOGNIZED : result;
      }
      /**
       * <pre>
       * 错误码
       * </pre>
       *
       * <code>.im.StatusCode code = 2;</code>
       * @param value The code to set.
       * @return This builder for chaining.
       */
      public Builder setCode(StatusCodeEnum.StatusCode value) {
        if (value == null) {
          throw new NullPointerException();
        }
        bitField0_ |= 0x00000002;
        code_ = value.getNumber();
        onChanged();
        return this;
      }
      /**
       * <pre>
       * 错误码
       * </pre>
       *
       * <code>.im.StatusCode code = 2;</code>
       * @return This builder for chaining.
       */
      public Builder clearCode() {
        bitField0_ = (bitField0_ & ~0x00000002);
        code_ = 0;
        onChanged();
        return this;
      }

      private Object message_ = "";
      /**
       * <pre>
       * 提示信息
       * </pre>
       *
       * <code>string message = 3;</code>
       * @return The message.
       */
      public String getMessage() {
        Object ref = message_;
        if (!(ref instanceof String)) {
          com.google.protobuf.ByteString bs =
              (com.google.protobuf.ByteString) ref;
          String s = bs.toStringUtf8();
          message_ = s;
          return s;
        } else {
          return (String) ref;
        }
      }
      /**
       * <pre>
       * 提示信息
       * </pre>
       *
       * <code>string message = 3;</code>
       * @return The bytes for message.
       */
      public com.google.protobuf.ByteString
          getMessageBytes() {
        Object ref = message_;
        if (ref instanceof String) {
          com.google.protobuf.ByteString b = 
              com.google.protobuf.ByteString.copyFromUtf8(
                  (String) ref);
          message_ = b;
          return b;
        } else {
          return (com.google.protobuf.ByteString) ref;
        }
      }
      /**
       * <pre>
       * 提示信息
       * </pre>
       *
       * <code>string message = 3;</code>
       * @param value The message to set.
       * @return This builder for chaining.
       */
      public Builder setMessage(
          String value) {
        if (value == null) { throw new NullPointerException(); }
        message_ = value;
        bitField0_ |= 0x00000004;
        onChanged();
        return this;
      }
      /**
       * <pre>
       * 提示信息
       * </pre>
       *
       * <code>string message = 3;</code>
       * @return This builder for chaining.
       */
      public Builder clearMessage() {
        message_ = getDefaultInstance().getMessage();
        bitField0_ = (bitField0_ & ~0x00000004);
        onChanged();
        return this;
      }
      /**
       * <pre>
       * 提示信息
       * </pre>
       *
       * <code>string message = 3;</code>
       * @param value The bytes for message to set.
       * @return This builder for chaining.
       */
      public Builder setMessageBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) { throw new NullPointerException(); }
        checkByteStringIsUtf8(value);
        message_ = value;
        bitField0_ |= 0x00000004;
        onChanged();
        return this;
      }

      private Object senderId_ = "";
      /**
       * <code>string sender_id = 4;</code>
       * @return The senderId.
       */
      public String getSenderId() {
        Object ref = senderId_;
        if (!(ref instanceof String)) {
          com.google.protobuf.ByteString bs =
              (com.google.protobuf.ByteString) ref;
          String s = bs.toStringUtf8();
          senderId_ = s;
          return s;
        } else {
          return (String) ref;
        }
      }
      /**
       * <code>string sender_id = 4;</code>
       * @return The bytes for senderId.
       */
      public com.google.protobuf.ByteString
          getSenderIdBytes() {
        Object ref = senderId_;
        if (ref instanceof String) {
          com.google.protobuf.ByteString b = 
              com.google.protobuf.ByteString.copyFromUtf8(
                  (String) ref);
          senderId_ = b;
          return b;
        } else {
          return (com.google.protobuf.ByteString) ref;
        }
      }
      /**
       * <code>string sender_id = 4;</code>
       * @param value The senderId to set.
       * @return This builder for chaining.
       */
      public Builder setSenderId(
          String value) {
        if (value == null) { throw new NullPointerException(); }
        senderId_ = value;
        bitField0_ |= 0x00000008;
        onChanged();
        return this;
      }
      /**
       * <code>string sender_id = 4;</code>
       * @return This builder for chaining.
       */
      public Builder clearSenderId() {
        senderId_ = getDefaultInstance().getSenderId();
        bitField0_ = (bitField0_ & ~0x00000008);
        onChanged();
        return this;
      }
      /**
       * <code>string sender_id = 4;</code>
       * @param value The bytes for senderId to set.
       * @return This builder for chaining.
       */
      public Builder setSenderIdBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) { throw new NullPointerException(); }
        checkByteStringIsUtf8(value);
        senderId_ = value;
        bitField0_ |= 0x00000008;
        onChanged();
        return this;
      }

      private Object receiverId_ = "";
      /**
       * <code>string receiver_id = 5;</code>
       * @return The receiverId.
       */
      public String getReceiverId() {
        Object ref = receiverId_;
        if (!(ref instanceof String)) {
          com.google.protobuf.ByteString bs =
              (com.google.protobuf.ByteString) ref;
          String s = bs.toStringUtf8();
          receiverId_ = s;
          return s;
        } else {
          return (String) ref;
        }
      }
      /**
       * <code>string receiver_id = 5;</code>
       * @return The bytes for receiverId.
       */
      public com.google.protobuf.ByteString
          getReceiverIdBytes() {
        Object ref = receiverId_;
        if (ref instanceof String) {
          com.google.protobuf.ByteString b = 
              com.google.protobuf.ByteString.copyFromUtf8(
                  (String) ref);
          receiverId_ = b;
          return b;
        } else {
          return (com.google.protobuf.ByteString) ref;
        }
      }
      /**
       * <code>string receiver_id = 5;</code>
       * @param value The receiverId to set.
       * @return This builder for chaining.
       */
      public Builder setReceiverId(
          String value) {
        if (value == null) { throw new NullPointerException(); }
        receiverId_ = value;
        bitField0_ |= 0x00000010;
        onChanged();
        return this;
      }
      /**
       * <code>string receiver_id = 5;</code>
       * @return This builder for chaining.
       */
      public Builder clearReceiverId() {
        receiverId_ = getDefaultInstance().getReceiverId();
        bitField0_ = (bitField0_ & ~0x00000010);
        onChanged();
        return this;
      }
      /**
       * <code>string receiver_id = 5;</code>
       * @param value The bytes for receiverId to set.
       * @return This builder for chaining.
       */
      public Builder setReceiverIdBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) { throw new NullPointerException(); }
        checkByteStringIsUtf8(value);
        receiverId_ = value;
        bitField0_ |= 0x00000010;
        onChanged();
        return this;
      }

      private long messageId_ ;
      /**
       * <pre>
       * 消息id
       * </pre>
       *
       * <code>int64 message_id = 6;</code>
       * @return The messageId.
       */
      @Override
      public long getMessageId() {
        return messageId_;
      }
      /**
       * <pre>
       * 消息id
       * </pre>
       *
       * <code>int64 message_id = 6;</code>
       * @param value The messageId to set.
       * @return This builder for chaining.
       */
      public Builder setMessageId(long value) {

        messageId_ = value;
        bitField0_ |= 0x00000020;
        onChanged();
        return this;
      }
      /**
       * <pre>
       * 消息id
       * </pre>
       *
       * <code>int64 message_id = 6;</code>
       * @return This builder for chaining.
       */
      public Builder clearMessageId() {
        bitField0_ = (bitField0_ & ~0x00000020);
        messageId_ = 0L;
        onChanged();
        return this;
      }
      @Override
      public final Builder setUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return super.setUnknownFields(unknownFields);
      }

      @Override
      public final Builder mergeUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return super.mergeUnknownFields(unknownFields);
      }


      // @@protoc_insertion_point(builder_scope:im.MessageForwardResponse)
    }

    // @@protoc_insertion_point(class_scope:im.MessageForwardResponse)
    private static final MessageForwardResponse DEFAULT_INSTANCE;
    static {
      DEFAULT_INSTANCE = new MessageForwardResponse();
    }

    public static MessageForwardResponse getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    private static final com.google.protobuf.Parser<MessageForwardResponse>
        PARSER = new com.google.protobuf.AbstractParser<MessageForwardResponse>() {
      @Override
      public MessageForwardResponse parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        Builder builder = newBuilder();
        try {
          builder.mergeFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          throw e.setUnfinishedMessage(builder.buildPartial());
        } catch (com.google.protobuf.UninitializedMessageException e) {
          throw e.asInvalidProtocolBufferException().setUnfinishedMessage(builder.buildPartial());
        } catch (java.io.IOException e) {
          throw new com.google.protobuf.InvalidProtocolBufferException(e)
              .setUnfinishedMessage(builder.buildPartial());
        }
        return builder.buildPartial();
      }
    };

    public static com.google.protobuf.Parser<MessageForwardResponse> parser() {
      return PARSER;
    }

    @Override
    public com.google.protobuf.Parser<MessageForwardResponse> getParserForType() {
      return PARSER;
    }

    @Override
    public MessageForwardResponse getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

  }

  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_im_MessageForwardResponse_descriptor;
  private static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_im_MessageForwardResponse_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    String[] descriptorData = {
      "\n\034MessageForwardResponse.proto\022\002im\032\020Stat" +
      "usCode.proto\"\224\001\n\026MessageForwardResponse\022" +
      "\017\n\007success\030\001 \001(\010\022\034\n\004code\030\002 \001(\0162\016.im.Stat" +
      "usCode\022\017\n\007message\030\003 \001(\t\022\021\n\tsender_id\030\004 \001" +
      "(\t\022\023\n\013receiver_id\030\005 \001(\t\022\022\n\nmessage_id\030\006 " +
      "\001(\003B6\n\027com.hiraeth.im.protocolB\033MessageF" +
      "orwardResponseProtob\006proto3"
    };
    descriptor = com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
          StatusCodeEnum.getDescriptor(),
        });
    internal_static_im_MessageForwardResponse_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_im_MessageForwardResponse_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_im_MessageForwardResponse_descriptor,
        new String[] { "Success", "Code", "Message", "SenderId", "ReceiverId", "MessageId", });
    StatusCodeEnum.getDescriptor();
  }

  // @@protoc_insertion_point(outer_class_scope)
}
