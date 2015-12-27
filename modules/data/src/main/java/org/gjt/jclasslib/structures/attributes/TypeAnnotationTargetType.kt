/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.structures.attributes

import org.gjt.jclasslib.structures.ClassFileEnum
import org.gjt.jclasslib.structures.InvalidByteCodeException
import org.gjt.jclasslib.structures.Lookup
import org.gjt.jclasslib.structures.attributes.targettype.*
import kotlin.reflect.KClass

/**
 * Represents the target type of a type annotation.
 */
enum class TypeAnnotationTargetType(override val tag: Int, private val targetInfoClass: KClass<out TargetInfo>) : ClassFileEnum {

    UNDEFINED(-1, UndefinedTargetInfo::class),
    GENERIC_PARAMETER_CLASS(0, ParameterTargetInfo::class),
    GENERIC_PARAMETER_METHOD(1, ParameterTargetInfo::class),
    SUPERTYPE(16, SupertypeTargetInfo::class),
    BOUND_GENERIC_PARAMETER_CLASS(17, TypeParameterBoundTargetInfo::class),
    BOUND_GENERIC_PARAMETER_METHOD(18, TypeParameterBoundTargetInfo::class),
    FIELD(19, EmptyTargetInfo::class),
    RETURN_TYPE_METHOD(20, EmptyTargetInfo::class),
    RECEIVER_TYPE_METHOD(21, EmptyTargetInfo::class),
    FORMAL_PARAMETER_METHOD(22, ParameterTargetInfo::class),
    THROWS(23, ExceptionTargetInfo::class),
    LOCAL_VARIABLE(64, LocalVarTargetInfo::class),
    LOCAL_RESOURCE(65, LocalVarTargetInfo::class),
    CATCH(66, ExceptionTargetInfo::class),
    INSTANCEOF(67, OffsetTargetInfo::class),
    NEW(68, OffsetTargetInfo::class),
    METHODREF_NEW(69, OffsetTargetInfo::class),
    METHODREF_IDENTIFIER_NEW(70, OffsetTargetInfo::class),
    CAST(71, TypeArgumentTargetInfo::class),
    TYPE_ARGUMENT_CONSTRUCTOR_INVOCATION(72, TypeArgumentTargetInfo::class),
    TYPE_ARGUMENT_METHOD_INVOCATION(73, TypeArgumentTargetInfo::class),
    TYPE_ARGUMENT_METHODREF_NEW(74, TypeArgumentTargetInfo::class),
    TYPE_ARGUMENT_METHODREF_IDENTIFIER(75, TypeArgumentTargetInfo::class);

    fun createTargetInfo(): TargetInfo {
        val objectInstance = targetInfoClass.objectInstance
        if (objectInstance != null) {
            return objectInstance
        } else {
            try {
                return targetInfoClass.java.newInstance()
            } catch (e: Exception) {
                throw RuntimeException(e)
            }
        }
    }

    companion object : Lookup<TypeAnnotationTargetType>() {

        override val enumClass: Class<TypeAnnotationTargetType>
            get() = TypeAnnotationTargetType::class.java

        override val name: String
            get() = "type annotation target type"

    }
}
