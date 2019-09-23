/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.structures.attributes

import org.gjt.jclasslib.structures.ClassFileEnum
import org.gjt.jclasslib.structures.Lookup
import org.gjt.jclasslib.structures.attributes.targettype.*

/**
 * Represents the target type of a type annotation.
 */
@Suppress("NOT_DOCUMENTED")
enum class TypeAnnotationTargetType(override val tag: Int, private val targetInfoClass: Class<out TargetInfo>) : ClassFileEnum {

    GENERIC_PARAMETER_CLASS(0, ParameterTargetInfo::class.java),
    GENERIC_PARAMETER_METHOD(1, ParameterTargetInfo::class.java),
    SUPERTYPE(16, SupertypeTargetInfo::class.java),
    BOUND_GENERIC_PARAMETER_CLASS(17, TypeParameterBoundTargetInfo::class.java),
    BOUND_GENERIC_PARAMETER_METHOD(18, TypeParameterBoundTargetInfo::class.java),
    FIELD(19, EmptyTargetInfo::class.java),
    RETURN_TYPE_METHOD(20, EmptyTargetInfo::class.java),
    RECEIVER_TYPE_METHOD(21, EmptyTargetInfo::class.java),
    FORMAL_PARAMETER_METHOD(22, ParameterTargetInfo::class.java),
    THROWS(23, ExceptionTargetInfo::class.java),
    LOCAL_VARIABLE(64, LocalVarTargetInfo::class.java),
    LOCAL_RESOURCE(65, LocalVarTargetInfo::class.java),
    CATCH(66, ExceptionTargetInfo::class.java),
    INSTANCEOF(67, OffsetTargetInfo::class.java),
    NEW(68, OffsetTargetInfo::class.java),
    METHODREF_NEW(69, OffsetTargetInfo::class.java),
    METHODREF_IDENTIFIER_NEW(70, OffsetTargetInfo::class.java),
    CAST(71, TypeArgumentTargetInfo::class.java),
    TYPE_ARGUMENT_CONSTRUCTOR_INVOCATION(72, TypeArgumentTargetInfo::class.java),
    TYPE_ARGUMENT_METHOD_INVOCATION(73, TypeArgumentTargetInfo::class.java),
    TYPE_ARGUMENT_METHODREF_NEW(74, TypeArgumentTargetInfo::class.java),
    TYPE_ARGUMENT_METHODREF_IDENTIFIER(75, TypeArgumentTargetInfo::class.java);

    /**
     * Create an associated [TargetInfo] instance.
     */
    fun createTargetInfo(): TargetInfo {
        return try {
            targetInfoClass.getDeclaredConstructor().newInstance()
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    companion object : Lookup<TypeAnnotationTargetType>(TypeAnnotationTargetType::class.java, "type annotation target type")
}
