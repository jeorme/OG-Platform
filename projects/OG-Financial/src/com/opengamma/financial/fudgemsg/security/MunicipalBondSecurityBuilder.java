/**
 * Copyright (C) 2011 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.fudgemsg.security;

import org.fudgemsg.FudgeMsg;
import org.fudgemsg.MutableFudgeMsg;
import org.fudgemsg.mapping.FudgeBuilder;
import org.fudgemsg.mapping.FudgeBuilderFor;
import org.fudgemsg.mapping.FudgeDeserializer;
import org.fudgemsg.mapping.FudgeSerializer;

import com.opengamma.financial.security.bond.MunicipalBondSecurity;
import com.opengamma.util.fudgemsg.AbstractFudgeBuilder;

/**
 * A Fudge builder for {@code MunicipalBondSecurity}.
 */
@FudgeBuilderFor(MunicipalBondSecurity.class)
public class MunicipalBondSecurityBuilder extends AbstractFudgeBuilder implements FudgeBuilder<MunicipalBondSecurity> {

  @Override
  public MutableFudgeMsg buildMessage(FudgeSerializer serializer, MunicipalBondSecurity object) {
    final MutableFudgeMsg msg = serializer.newMessage();
    MunicipalBondSecurityBuilder.toFudgeMsg(serializer, object, msg);
    return msg;
  }

  public static void toFudgeMsg(FudgeSerializer serializer, MunicipalBondSecurity object, final MutableFudgeMsg msg) {
    BondSecurityBuilder.toFudgeMsg(serializer, object, msg);
  }

  @Override
  public MunicipalBondSecurity buildObject(FudgeDeserializer deserializer, FudgeMsg msg) {
    MunicipalBondSecurity object = FinancialSecurityBuilder.backdoorCreateClass(MunicipalBondSecurity.class);
    MunicipalBondSecurityBuilder.fromFudgeMsg(deserializer, msg, object);
    return object;
  }

  public static void fromFudgeMsg(FudgeDeserializer deserializer, FudgeMsg msg, MunicipalBondSecurity object) {
    BondSecurityBuilder.fromFudgeMsg(deserializer, msg, object);
  }

}
