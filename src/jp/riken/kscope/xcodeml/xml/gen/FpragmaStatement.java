/*
 * K-scope
 * Copyright 2012-2013 RIKEN, Japan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jp.riken.kscope.xcodeml.xml.gen;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import jp.riken.kscope.xcodeml.xml.*;

/**
 * Java class for anonymous complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType>
 *   &lt;simpleContent>
 *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
 *       &lt;attGroup ref="{}defAttrSourceLine"/>
 *     &lt;/extension>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
    name = "",
    propOrder = {"value"})
@XmlRootElement(name = "FpragmaStatement")
public class FpragmaStatement implements IDefBaseStatement, IXmlNode {

  @XmlValue protected String value;
  @XmlAttribute protected String lineno;
  @XmlAttribute protected String endlineno;
  @XmlAttribute protected String rawlineno;
  @XmlAttribute protected String file;

  /**
   * Gets the value of the value property.
   *
   * @return possible object is {@link String }
   */
  public String getValue() {
    return value;
  }

  /**
   * Sets the value of the value property.
   *
   * @param value allowed object is {@link String }
   */
  public void setValue(String value) {
    this.value = value;
  }

  /**
   * Gets the value of the lineno property.
   *
   * @return possible object is {@link String }
   */
  public String getLineno() {
    return lineno;
  }

  /**
   * Sets the value of the lineno property.
   *
   * @param value allowed object is {@link String }
   */
  public void setLineno(String value) {
    this.lineno = value;
  }

  /**
   * Gets the value of the endlineno property.
   *
   * @return possible object is {@link String }
   */
  public String getEndlineno() {
    return endlineno;
  }

  /**
   * Sets the value of the endlineno property.
   *
   * @param value allowed object is {@link String }
   */
  public void setEndlineno(String value) {
    this.endlineno = value;
  }

  /**
   * Gets the value of the rawlineno property.
   *
   * @return possible object is {@link String }
   */
  public String getRawlineno() {
    return rawlineno;
  }

  /**
   * Sets the value of the rawlineno property.
   *
   * @param value allowed object is {@link String }
   */
  public void setRawlineno(String value) {
    this.rawlineno = value;
  }

  /**
   * Gets the value of the file property.
   *
   * @return possible object is {@link String }
   */
  public String getFile() {
    return file;
  }

  /**
   * Sets the value of the file property.
   *
   * @param value allowed object is {@link String }
   */
  public void setFile(String value) {
    this.file = value;
  }

  @Override
  public boolean enter(jp.riken.kscope.xcodeml.xml.IXmlVisitor visitor) {
    return (visitor.enter(this));
  }

  @Override
  public void leave(jp.riken.kscope.xcodeml.xml.IXmlVisitor visitor) {
    visitor.leave(this);
  }
}
