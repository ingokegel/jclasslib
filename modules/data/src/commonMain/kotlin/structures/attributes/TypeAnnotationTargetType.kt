/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license or (at your option) any later version.
 */

package org.gjt.jclasslib.structures.attributes

import org.gjt.jclasslib.structures.ClassFileEnum
import org.gjt.jclasslib.structures.Lookup
import org.gjt.jclasslib.structures.attributes.targettype.*
import kotlin.enums.enumEntries

/**
 * Represents the target type of a type annotation.
 */
@Suppress("NOT_DOCUMENTED")
enum class TypeAnnotationTargetType(override val tag: Int, private val factory: () -> TargetInfo) : ClassFileEnum {

    GENERIC_PARAMETER_CLASS(0, { ParameterTargetInfo() }),
    GENERIC_PARAMETER_METHOD(1, { ParameterTargetInfo() } ),
    SUPERTYPE(16, { SupertypeTargetInfo() } ),
    BOUND_GENERIC_PARAMETER_CLASS(17, { TypeParameterBoundTargetInfo() } ),
    BOUND_GENERIC_PARAMETER_METHOD(18, { TypeParameterBoundTargetInfo() } ),
    FIELD(19, { EmptyTargetInfo() } ),
    RETURN_TYPE_METHOD(20, { EmptyTargetInfo() } ),
    RECEIVER_TYPE_METHOD(21, { EmptyTargetInfo() } ),
    FORMAL_PARAMETER_METHOD(22, { ParameterTargetInfo() } ),
    THROWS(23, { ExceptionTargetInfo() } ),
    LOCAL_VARIABLE(64, { LocalVarTargetInfo() } ),
    LOCAL_RESOURCE(65, { LocalVarTargetInfo() } ),
    CATCH(66, { ExceptionTargetInfo() } ),
    INSTANCEOF(67, { OffsetTargetInfo() } ),
    NEW(68, { OffsetTargetInfo() } ),
    METHODREF_NEW(69, { OffsetTargetInfo() } ),
    METHODREF_IDENTIFIER_NEW(70, { OffsetTargetInfo() } ),
    CAST(71, { TypeArgumentTargetInfo() } ),
    TYPE_ARGUMENT_CONSTRUCTOR_INVOCATION(72, { TypeArgumentTargetInfo() } ),
    TYPE_ARGUMENT_METHOD_INVOCATION(73, { TypeArgumentTargetInfo() } ),
    TYPE_ARGUMENT_METHODREF_NEW(74, { TypeArgumentTargetInfo() } ),
    TYPE_ARGUMENT_METHODREF_IDENTIFIER(75, { TypeArgumentTargetInfo() } );

    /**
     * Create an associated [TargetInfo] instance.
     */
    fun createTargetInfo(): TargetInfo = factory()

    companion object : Lookup<TypeAnnotationTargetType>(enumEntries<TypeAnnotationTargetType>(), "type annotation target type")
}
