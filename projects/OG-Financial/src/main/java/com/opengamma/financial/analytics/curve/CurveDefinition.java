/**
 * Copyright (C) 2013 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.analytics.curve;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

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

import com.opengamma.core.config.Config;
import com.opengamma.financial.analytics.ircurve.strips.CurveNode;
import com.opengamma.id.MutableUniqueIdentifiable;
import com.opengamma.id.UniqueId;
import com.opengamma.id.UniqueIdentifiable;
import org.joda.beans.Bean;

/**
 * Basic curve definition class containing only a name and curve nodes. Most curve definitions (e.g. {@link InterpolatedCurveDefinition}) will descend from this class,
 * but this can be used as a nodal curve definition.
 */
@BeanDefinition
@Config
public class CurveDefinition extends DirectBean implements Serializable, UniqueIdentifiable, MutableUniqueIdentifiable {

  /** Serialization version */
  private static final long serialVersionUID = 1L;

  /**
   * The unique identifier of the curve.
   */
  @PropertyDefinition
  private UniqueId _uniqueId;

  /**
   * The name of the curve.
   */
  @PropertyDefinition(validate = "notNull")
  private String _name;

  /**
   * The constituents of the curve.
   */
  @PropertyDefinition(validate = "notNull")
  private SortedSet<CurveNode> _nodes = new TreeSet<>();

  /**
   * For the builder
   */
  public CurveDefinition() {
  }

  /**
   * @param name The name of the curve definition, not null
   * @param nodes The curve nodes, not null
   */
  public CurveDefinition(final String name, final Set<CurveNode> nodes) {
    setName(name);
    setNodes(new TreeSet<>(nodes));
  }
  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code CurveDefinition}.
   * @return the meta-bean, not null
   */
  public static CurveDefinition.Meta meta() {
    return CurveDefinition.Meta.INSTANCE;
  }

  static {
    JodaBeanUtils.registerMetaBean(CurveDefinition.Meta.INSTANCE);
  }

  @Override
  public CurveDefinition.Meta metaBean() {
    return CurveDefinition.Meta.INSTANCE;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the unique identifier of the curve.
   * @return the value of the property
   */
  public UniqueId getUniqueId() {
    return _uniqueId;
  }

  /**
   * Sets the unique identifier of the curve.
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
   * Gets the name of the curve.
   * @return the value of the property, not null
   */
  public String getName() {
    return _name;
  }

  /**
   * Sets the name of the curve.
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
   * Gets the constituents of the curve.
   * @return the value of the property, not null
   */
  public SortedSet<CurveNode> getNodes() {
    return _nodes;
  }

  /**
   * Sets the constituents of the curve.
   * @param nodes  the new value of the property, not null
   */
  public void setNodes(SortedSet<CurveNode> nodes) {
    JodaBeanUtils.notNull(nodes, "nodes");
    this._nodes = nodes;
  }

  /**
   * Gets the the {@code nodes} property.
   * @return the property, not null
   */
  public final Property<SortedSet<CurveNode>> nodes() {
    return metaBean().nodes().createProperty(this);
  }

  //-----------------------------------------------------------------------
  @Override
  public CurveDefinition clone() {
    BeanBuilder<? extends CurveDefinition> builder = metaBean().builder();
    for (MetaProperty<?> mp : metaBean().metaPropertyIterable()) {
      if (mp.style().isBuildable()) {
        Object value = mp.get(this);
        if (value instanceof Bean) {
          value = ((Bean) value).clone();
        }
        builder.set(mp.name(), value);
      }
    }
    return builder.build();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      CurveDefinition other = (CurveDefinition) obj;
      return JodaBeanUtils.equal(getUniqueId(), other.getUniqueId()) &&
          JodaBeanUtils.equal(getName(), other.getName()) &&
          JodaBeanUtils.equal(getNodes(), other.getNodes());
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = getClass().hashCode();
    hash += hash * 31 + JodaBeanUtils.hashCode(getUniqueId());
    hash += hash * 31 + JodaBeanUtils.hashCode(getName());
    hash += hash * 31 + JodaBeanUtils.hashCode(getNodes());
    return hash;
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder(128);
    buf.append("CurveDefinition{");
    int len = buf.length();
    toString(buf);
    if (buf.length() > len) {
      buf.setLength(buf.length() - 2);
    }
    buf.append('}');
    return buf.toString();
  }

  protected void toString(StringBuilder buf) {
    buf.append("uniqueId").append('=').append(getUniqueId()).append(',').append(' ');
    buf.append("name").append('=').append(getName()).append(',').append(' ');
    buf.append("nodes").append('=').append(getNodes()).append(',').append(' ');
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code CurveDefinition}.
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
        this, "uniqueId", CurveDefinition.class, UniqueId.class);
    /**
     * The meta-property for the {@code name} property.
     */
    private final MetaProperty<String> _name = DirectMetaProperty.ofReadWrite(
        this, "name", CurveDefinition.class, String.class);
    /**
     * The meta-property for the {@code nodes} property.
     */
    @SuppressWarnings({"unchecked", "rawtypes" })
    private final MetaProperty<SortedSet<CurveNode>> _nodes = DirectMetaProperty.ofReadWrite(
        this, "nodes", CurveDefinition.class, (Class) SortedSet.class);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> _metaPropertyMap$ = new DirectMetaPropertyMap(
        this, null,
        "uniqueId",
        "name",
        "nodes");

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
        case 3373707:  // name
          return _name;
        case 104993457:  // nodes
          return _nodes;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public BeanBuilder<? extends CurveDefinition> builder() {
      return new DirectBeanBuilder<CurveDefinition>(new CurveDefinition());
    }

    @Override
    public Class<? extends CurveDefinition> beanType() {
      return CurveDefinition.class;
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
      return _metaPropertyMap$;
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
     * The meta-property for the {@code name} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<String> name() {
      return _name;
    }

    /**
     * The meta-property for the {@code nodes} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<SortedSet<CurveNode>> nodes() {
      return _nodes;
    }

    //-----------------------------------------------------------------------
    @Override
    protected Object propertyGet(Bean bean, String propertyName, boolean quiet) {
      switch (propertyName.hashCode()) {
        case -294460212:  // uniqueId
          return ((CurveDefinition) bean).getUniqueId();
        case 3373707:  // name
          return ((CurveDefinition) bean).getName();
        case 104993457:  // nodes
          return ((CurveDefinition) bean).getNodes();
      }
      return super.propertyGet(bean, propertyName, quiet);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void propertySet(Bean bean, String propertyName, Object newValue, boolean quiet) {
      switch (propertyName.hashCode()) {
        case -294460212:  // uniqueId
          ((CurveDefinition) bean).setUniqueId((UniqueId) newValue);
          return;
        case 3373707:  // name
          ((CurveDefinition) bean).setName((String) newValue);
          return;
        case 104993457:  // nodes
          ((CurveDefinition) bean).setNodes((SortedSet<CurveNode>) newValue);
          return;
      }
      super.propertySet(bean, propertyName, newValue, quiet);
    }

    @Override
    protected void validate(Bean bean) {
      JodaBeanUtils.notNull(((CurveDefinition) bean)._name, "name");
      JodaBeanUtils.notNull(((CurveDefinition) bean)._nodes, "nodes");
    }

  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}
