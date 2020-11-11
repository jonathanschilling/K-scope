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

package jp.riken.kscope.language;

/**
 * A class that represents the main program unit.
 *
 * @author RIKEN
 */
public class Main extends Procedure {
  /** Serial number */
  private static final long serialVersionUID = 990753709114089611L;
  /**
   * Constructor.
   *
   * @param name Main name
   */
  public Main(String name) {
    super("main", name);
  }
}
