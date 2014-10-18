// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: descriptor.proto

package com.google.protobuf;

public interface FieldOptionsOrBuilder
		extends
		com.google.protobuf.GeneratedMessage.ExtendableMessageOrBuilder<FieldOptions> {

	// optional .google.protobuf.FieldOptions.CType ctype = 1 [default =
	// STRING];
	/**
	 * <code>optional .google.protobuf.FieldOptions.CType ctype = 1 [default = STRING];</code>
	 * 
	 * <pre>
	 * The ctype option instructs the C++ code generator to use a different
	 * representation of the field than it normally would.  See the specific
	 * options below.  This option is not yet implemented in the open source
	 * release -- sorry, we'll try to include it in a future version!
	 * </pre>
	 */
	boolean hasCtype();

	/**
	 * <code>optional .google.protobuf.FieldOptions.CType ctype = 1 [default = STRING];</code>
	 * 
	 * <pre>
	 * The ctype option instructs the C++ code generator to use a different
	 * representation of the field than it normally would.  See the specific
	 * options below.  This option is not yet implemented in the open source
	 * release -- sorry, we'll try to include it in a future version!
	 * </pre>
	 */
	com.google.protobuf.FieldOptions.CType getCtype();

	// optional bool packed = 2;
	/**
	 * <code>optional bool packed = 2;</code>
	 * 
	 * <pre>
	 * The packed option can be enabled for repeated primitive fields to enable
	 * a more efficient representation on the wire. Rather than repeatedly
	 * writing the tag and type for each element, the entire array is encoded as
	 * a single length-delimited blob.
	 * </pre>
	 */
	boolean hasPacked();

	/**
	 * <code>optional bool packed = 2;</code>
	 * 
	 * <pre>
	 * The packed option can be enabled for repeated primitive fields to enable
	 * a more efficient representation on the wire. Rather than repeatedly
	 * writing the tag and type for each element, the entire array is encoded as
	 * a single length-delimited blob.
	 * </pre>
	 */
	boolean getPacked();

	// optional bool lazy = 5 [default = false];
	/**
	 * <code>optional bool lazy = 5 [default = false];</code>
	 * 
	 * <pre>
	 * Should this field be parsed lazily?  Lazy applies only to message-type
	 * fields.  It means that when the outer message is initially parsed, the
	 * inner message's contents will not be parsed but instead stored in encoded
	 * form.  The inner message will actually be parsed when it is first accessed.
	 * 
	 * This is only a hint.  Implementations are free to choose whether to use
	 * eager or lazy parsing regardless of the value of this option.  However,
	 * setting this option true suggests that the protocol author believes that
	 * using lazy parsing on this field is worth the additional bookkeeping
	 * overhead typically needed to implement it.
	 * 
	 * This option does not affect the public interface of any generated code;
	 * all method signatures remain the same.  Furthermore, thread-safety of the
	 * interface is not affected by this option; const methods remain safe to
	 * call from multiple threads concurrently, while non-const methods continue
	 * to require exclusive access.
	 * 
	 * 
	 * Note that implementations may choose not to check required fields within
	 * a lazy sub-message.  That is, calling IsInitialized() on the outher message
	 * may return true even if the inner message has missing required fields.
	 * This is necessary because otherwise the inner message would have to be
	 * parsed in order to perform the check, defeating the purpose of lazy
	 * parsing.  An implementation which chooses not to check required fields
	 * must be consistent about it.  That is, for any particular sub-message, the
	 * implementation must either *always* check its required fields, or *never*
	 * check its required fields, regardless of whether or not the message has
	 * been parsed.
	 * </pre>
	 */
	boolean hasLazy();

	/**
	 * <code>optional bool lazy = 5 [default = false];</code>
	 * 
	 * <pre>
	 * Should this field be parsed lazily?  Lazy applies only to message-type
	 * fields.  It means that when the outer message is initially parsed, the
	 * inner message's contents will not be parsed but instead stored in encoded
	 * form.  The inner message will actually be parsed when it is first accessed.
	 * 
	 * This is only a hint.  Implementations are free to choose whether to use
	 * eager or lazy parsing regardless of the value of this option.  However,
	 * setting this option true suggests that the protocol author believes that
	 * using lazy parsing on this field is worth the additional bookkeeping
	 * overhead typically needed to implement it.
	 * 
	 * This option does not affect the public interface of any generated code;
	 * all method signatures remain the same.  Furthermore, thread-safety of the
	 * interface is not affected by this option; const methods remain safe to
	 * call from multiple threads concurrently, while non-const methods continue
	 * to require exclusive access.
	 * 
	 * 
	 * Note that implementations may choose not to check required fields within
	 * a lazy sub-message.  That is, calling IsInitialized() on the outher message
	 * may return true even if the inner message has missing required fields.
	 * This is necessary because otherwise the inner message would have to be
	 * parsed in order to perform the check, defeating the purpose of lazy
	 * parsing.  An implementation which chooses not to check required fields
	 * must be consistent about it.  That is, for any particular sub-message, the
	 * implementation must either *always* check its required fields, or *never*
	 * check its required fields, regardless of whether or not the message has
	 * been parsed.
	 * </pre>
	 */
	boolean getLazy();

	// optional bool deprecated = 3 [default = false];
	/**
	 * <code>optional bool deprecated = 3 [default = false];</code>
	 * 
	 * <pre>
	 * Is this field deprecated?
	 * Depending on the target platform, this can emit Deprecated annotations
	 * for accessors, or it will be completely ignored; in the very least, this
	 * is a formalization for deprecating fields.
	 * </pre>
	 */
	boolean hasDeprecated();

	/**
	 * <code>optional bool deprecated = 3 [default = false];</code>
	 * 
	 * <pre>
	 * Is this field deprecated?
	 * Depending on the target platform, this can emit Deprecated annotations
	 * for accessors, or it will be completely ignored; in the very least, this
	 * is a formalization for deprecating fields.
	 * </pre>
	 */
	boolean getDeprecated();

	// optional string experimental_map_key = 9;
	/**
	 * <code>optional string experimental_map_key = 9;</code>
	 * 
	 * <pre>
	 * EXPERIMENTAL.  DO NOT USE.
	 * For "map" fields, the name of the field in the enclosed type that
	 * is the key for this map.  For example, suppose we have:
	 *   message Item {
	 *     required string name = 1;
	 *     required string value = 2;
	 *   }
	 *   message Config {
	 *     repeated Item items = 1 [experimental_map_key="name"];
	 *   }
	 * In this situation, the map key for Item will be set to "name".
	 * TODO: Fully-implement this, then remove the "experimental_" prefix.
	 * </pre>
	 */
	boolean hasExperimentalMapKey();

	/**
	 * <code>optional string experimental_map_key = 9;</code>
	 * 
	 * <pre>
	 * EXPERIMENTAL.  DO NOT USE.
	 * For "map" fields, the name of the field in the enclosed type that
	 * is the key for this map.  For example, suppose we have:
	 *   message Item {
	 *     required string name = 1;
	 *     required string value = 2;
	 *   }
	 *   message Config {
	 *     repeated Item items = 1 [experimental_map_key="name"];
	 *   }
	 * In this situation, the map key for Item will be set to "name".
	 * TODO: Fully-implement this, then remove the "experimental_" prefix.
	 * </pre>
	 */
	java.lang.String getExperimentalMapKey();

	/**
	 * <code>optional string experimental_map_key = 9;</code>
	 * 
	 * <pre>
	 * EXPERIMENTAL.  DO NOT USE.
	 * For "map" fields, the name of the field in the enclosed type that
	 * is the key for this map.  For example, suppose we have:
	 *   message Item {
	 *     required string name = 1;
	 *     required string value = 2;
	 *   }
	 *   message Config {
	 *     repeated Item items = 1 [experimental_map_key="name"];
	 *   }
	 * In this situation, the map key for Item will be set to "name".
	 * TODO: Fully-implement this, then remove the "experimental_" prefix.
	 * </pre>
	 */
	com.google.protobuf.ByteString getExperimentalMapKeyBytes();

	// optional bool weak = 10 [default = false];
	/**
	 * <code>optional bool weak = 10 [default = false];</code>
	 * 
	 * <pre>
	 * For Google-internal migration only. Do not use.
	 * </pre>
	 */
	boolean hasWeak();

	/**
	 * <code>optional bool weak = 10 [default = false];</code>
	 * 
	 * <pre>
	 * For Google-internal migration only. Do not use.
	 * </pre>
	 */
	boolean getWeak();

	// repeated .google.protobuf.UninterpretedOption uninterpreted_option = 999;
	/**
	 * <code>repeated .google.protobuf.UninterpretedOption uninterpreted_option = 999;</code>
	 * 
	 * <pre>
	 * The parser stores options it doesn't recognize here. See above.
	 * </pre>
	 */
	java.util.List<com.google.protobuf.UninterpretedOption> getUninterpretedOptionList();

	/**
	 * <code>repeated .google.protobuf.UninterpretedOption uninterpreted_option = 999;</code>
	 * 
	 * <pre>
	 * The parser stores options it doesn't recognize here. See above.
	 * </pre>
	 */
	com.google.protobuf.UninterpretedOption getUninterpretedOption(int index);

	/**
	 * <code>repeated .google.protobuf.UninterpretedOption uninterpreted_option = 999;</code>
	 * 
	 * <pre>
	 * The parser stores options it doesn't recognize here. See above.
	 * </pre>
	 */
	int getUninterpretedOptionCount();

	/**
	 * <code>repeated .google.protobuf.UninterpretedOption uninterpreted_option = 999;</code>
	 * 
	 * <pre>
	 * The parser stores options it doesn't recognize here. See above.
	 * </pre>
	 */
	java.util.List<? extends com.google.protobuf.UninterpretedOptionOrBuilder> getUninterpretedOptionOrBuilderList();

	/**
	 * <code>repeated .google.protobuf.UninterpretedOption uninterpreted_option = 999;</code>
	 * 
	 * <pre>
	 * The parser stores options it doesn't recognize here. See above.
	 * </pre>
	 */
	com.google.protobuf.UninterpretedOptionOrBuilder getUninterpretedOptionOrBuilder(
			int index);
}