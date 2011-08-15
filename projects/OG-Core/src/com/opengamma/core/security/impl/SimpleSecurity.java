/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.core.security.impl;

import java.io.Serializable;
import java.util.Map;

import org.joda.beans.BeanBuilder;
import org.joda.beans.BeanDefinition;
import org.joda.beans.JodaBeanUtils;
import org.joda.beans.MetaProperty;
import org.joda.beans.Property;
import org.joda.beans.PropertyDefinition;
import org.joda.beans.impl.direct.DirectBean;
import org.joda.beans.impl.direct.DirectBeanBuilder;
import org.joda.beans.impl.direct.DirectMetaBean;
import org.joda.beans.impl.direct.DirectMetaProperty;
import org.joda.beans.impl.direct.DirectMetaPropertyMap;

import com.opengamma.core.security.Security;
import com.opengamma.id.ExternalId;
import com.opengamma.id.ExternalIdBundle;
import com.opengamma.id.MutableUniqueIdentifiable;
import com.opengamma.id.UniqueId;

/**
 * Simple implementation of {@code Security}.
 * <p>
 * This is the simplest possible implementation of the {@link Security} interface.
 * <p>
 * This class is mutable and not thread-safe.
 * It is intended to be used in the engine via the read-only {@code Security} interface.
 */
@BeanDefinition
public class SimpleSecurity extends DirectBean
    implements Security, MutableUniqueIdentifiable, Serializable {

  /** Serialization version. */
  private static final long serialVersionUID = 1L;

  /**
   * The unique identifier.
   */
  @PropertyDefinition
  private UniqueId _uniqueId;
  /**
   * The external identifier bundle.
   */
  @PropertyDefinition(validate = "notNull")
  private ExternalIdBundle _identifiers = ExternalIdBundle.EMPTY;
  /**
   * The security type.
   */
  @PropertyDefinition(validate = "notNull")
  private String _securityType;
  /**
   * The display name.
   */
  @PropertyDefinition(validate = "notNull")
  private String _name = "";

  /**
   * Creates an instance.
   */
  private SimpleSecurity() {
  }

  /**
   * Creates an instance.
   * 
   * @param securityType  the security type, not null
   */
  public SimpleSecurity(String securityType) {
    setSecurityType(securityType);
  }

  /**
   * Creates an instance.
   * 
   * @param uniqueId  the unique identifier, may be null
   * @param bundle  the external identifier bundle, not null
   * @param securityType  the security type, not null
   * @param name  the display name, not null
   */
  public SimpleSecurity(UniqueId uniqueId, ExternalIdBundle bundle, String securityType, String name) {
    setUniqueId(uniqueId);
    setIdentifiers(bundle);
    setSecurityType(securityType);
    setName(name);
  }

  //-------------------------------------------------------------------------
  /**
   * Adds an external identifier to the bundle.
   * 
   * @param externalId  the external identifier, not null
   */
  public void addExternalId(final ExternalId externalId) {
    setIdentifiers(getIdentifiers().withExternalId(externalId));
  }

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code SimpleSecurity}.
   * @return the meta-bean, not null
   */
  public static SimpleSecurity.Meta meta() {
    return SimpleSecurity.Meta.INSTANCE;
  }
  static {
    JodaBeanUtils.registerMetaBean(SimpleSecurity.Meta.INSTANCE);
  }

  @Override
  public SimpleSecurity.Meta metaBean() {
    return SimpleSecurity.Meta.INSTANCE;
  }

  @Override
  protected Object propertyGet(String propertyName, boolean quiet) {
    switch (propertyName.hashCode()) {
      case -294460212:  // uniqueId
        return getUniqueId();
      case 1368189162:  // identifiers
        return getIdentifiers();
      case 808245914:  // securityType
        return getSecurityType();
      case 3373707:  // name
        return getName();
    }
    return super.propertyGet(propertyName, quiet);
  }

  @Override
  protected void propertySet(String propertyName, Object newValue, boolean quiet) {
    switch (propertyName.hashCode()) {
      case -294460212:  // uniqueId
        setUniqueId((UniqueId) newValue);
        return;
      case 1368189162:  // identifiers
        setIdentifiers((ExternalIdBundle) newValue);
        return;
      case 808245914:  // securityType
        setSecurityType((String) newValue);
        return;
      case 3373707:  // name
        setName((String) newValue);
        return;
    }
    super.propertySet(propertyName, newValue, quiet);
  }

  @Override
  protected void validate() {
    JodaBeanUtils.notNull(_identifiers, "identifiers");
    JodaBeanUtils.notNull(_securityType, "securityType");
    JodaBeanUtils.notNull(_name, "name");
    super.validate();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      SimpleSecurity other = (SimpleSecurity) obj;
      return JodaBeanUtils.equal(getUniqueId(), other.getUniqueId()) &&
          JodaBeanUtils.equal(getIdentifiers(), other.getIdentifiers()) &&
          JodaBeanUtils.equal(getSecurityType(), other.getSecurityType()) &&
          JodaBeanUtils.equal(getName(), other.getName());
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = getClass().hashCode();
    hash += hash * 31 + JodaBeanUtils.hashCode(getUniqueId());
    hash += hash * 31 + JodaBeanUtils.hashCode(getIdentifiers());
    hash += hash * 31 + JodaBeanUtils.hashCode(getSecurityType());
    hash += hash * 31 + JodaBeanUtils.hashCode(getName());
    return hash;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the unique identifier.
   * @return the value of the property
   */
  public UniqueId getUniqueId() {
    return _uniqueId;
  }

  /**
   * Sets the unique identifier.
   * @param uniqueId  the new value of the property
   */
  public void setUniqueId(UniqueId uniqueId) {
    this._uniqueId = uniqueId;
  }

  /**
   * Gets the the {@code uniqueId} property.
   * @return the property, not null
   */
  public final Property<UniqueId> uniqueId() {
    return metaBean().uniqueId().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the external identifier bundle.
   * @return the value of the property, not null
   */
  public ExternalIdBundle getIdentifiers() {
    return _identifiers;
  }

  /**
   * Sets the external identifier bundle.
   * @param identifiers  the new value of the property, not null
   */
  public void setIdentifiers(ExternalIdBundle identifiers) {
    JodaBeanUtils.notNull(identifiers, "identifiers");
    this._identifiers = identifiers;
  }

  /**
   * Gets the the {@code identifiers} property.
   * @return the property, not null
   */
  public final Property<ExternalIdBundle> identifiers() {
    return metaBean().identifiers().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the security type.
   * @return the value of the property, not null
   */
  public String getSecurityType() {
    return _securityType;
  }

  /**
   * Sets the security type.
   * @param securityType  the new value of the property, not null
   */
  public void setSecurityType(String securityType) {
    JodaBeanUtils.notNull(securityType, "securityType");
    this._securityType = securityType;
  }

  /**
   * Gets the the {@code securityType} property.
   * @return the property, not null
   */
  public final Property<String> securityType() {
    return metaBean().securityType().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the display name.
   * @return the value of the property, not null
   */
  public String getName() {
    return _name;
  }

  /**
   * Sets the display name.
   * @param name  the new value of the property, not null
   */
  public void setName(String name) {
    JodaBeanUtils.notNull(name, "name");
    this._name = name;
  }

  /**
   * Gets the the {@code name} property.
   * @return the property, not null
   */
  public final Property<String> name() {
    return metaBean().name().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code SimpleSecurity}.
   */
  public static class Meta extends DirectMetaBean {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code uniqueId} property.
     */
    private final MetaProperty<UniqueId> _uniqueId = DirectMetaProperty.ofReadWrite(
        this, "uniqueId", SimpleSecurity.class, UniqueId.class);
    /**
     * The meta-property for the {@code identifiers} property.
     */
    private final MetaProperty<ExternalIdBundle> _identifiers = DirectMetaProperty.ofReadWrite(
        this, "identifiers", SimpleSecurity.class, ExternalIdBundle.class);
    /**
     * The meta-property for the {@code securityType} property.
     */
    private final MetaProperty<String> _securityType = DirectMetaProperty.ofReadWrite(
        this, "securityType", SimpleSecurity.class, String.class);
    /**
     * The meta-property for the {@code name} property.
     */
    private final MetaProperty<String> _name = DirectMetaProperty.ofReadWrite(
        this, "name", SimpleSecurity.class, String.class);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<Object>> _map = new DirectMetaPropertyMap(
        this, null,
        "uniqueId",
        "identifiers",
        "securityType",
        "name");

    /**
     * Restricted constructor.
     */
    protected Meta() {
    }

    @Override
    protected MetaProperty<?> metaPropertyGet(String propertyName) {
      switch (propertyName.hashCode()) {
        case -294460212:  // uniqueId
          return _uniqueId;
        case 1368189162:  // identifiers
          return _identifiers;
        case 808245914:  // securityType
          return _securityType;
        case 3373707:  // name
          return _name;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public BeanBuilder<? extends SimpleSecurity> builder() {
      return new DirectBeanBuilder<SimpleSecurity>(new SimpleSecurity());
    }

    @Override
    public Class<? extends SimpleSecurity> beanType() {
      return SimpleSecurity.class;
    }

    @Override
    public Map<String, MetaProperty<Object>> metaPropertyMap() {
      return _map;
    }

    //-----------------------------------------------------------------------
    /**
     * The meta-property for the {@code uniqueId} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<UniqueId> uniqueId() {
      return _uniqueId;
    }

    /**
     * The meta-property for the {@code identifiers} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<ExternalIdBundle> identifiers() {
      return _identifiers;
    }

    /**
     * The meta-property for the {@code securityType} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<String> securityType() {
      return _securityType;
    }

    /**
     * The meta-property for the {@code name} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<String> name() {
      return _name;
    }

  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}
