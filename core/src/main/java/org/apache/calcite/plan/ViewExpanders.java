/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to you under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.calcite.plan;

import org.apache.calcite.rel.RelRoot;
import org.apache.calcite.rel.hint.RelHint;
import org.apache.calcite.rel.type.RelDataType;

import com.google.common.collect.ImmutableList;

import java.util.List;
import javax.annotation.Nonnull;

/**
 * Utilities for {@link RelOptTable.ViewExpander} and
 * {@link RelOptTable.ToRelContext}.
 */
@Nonnull
public abstract class ViewExpanders {
  private ViewExpanders() {}

  /** Converts a {@code ViewExpander} to a {@code ToRelContext}. */
  public static RelOptTable.ToRelContext toRelContext(
      RelOptTable.ViewExpander viewExpander,
      RelOptCluster cluster,
      List<RelHint> hints) {
    // See if the user wants to customize the ToRelContext.
    if (viewExpander instanceof RelOptTable.ToRelContextFactory) {
      return ((RelOptTable.ToRelContextFactory) viewExpander)
          .createToRelContext(viewExpander, cluster, hints);
    }
    return new RelOptTable.ToRelContext() {
      public RelOptCluster getCluster() {
        return cluster;
      }

      public List<RelHint> getTableHints() {
        return hints;
      }

      public RelRoot expandView(RelDataType rowType, String queryString,
          List<String> schemaPath, List<String> viewPath) {
        return viewExpander.expandView(rowType, queryString, schemaPath,
            viewPath);
      }
    };
  }

  /** Converts a {@code ViewExpander} to a {@code ToRelContext}. */
  public static RelOptTable.ToRelContext toRelContext(
      RelOptTable.ViewExpander viewExpander,
      RelOptCluster cluster) {
    return toRelContext(viewExpander, cluster, ImmutableList.of());
  }

  /** Creates a simple {@code ToRelContext} that cannot expand views. */
  public static RelOptTable.ToRelContext simpleContext(RelOptCluster cluster) {
    return new RelOptTable.ToRelContext() {
      public RelOptCluster getCluster() {
        return cluster;
      }

      public RelRoot expandView(RelDataType rowType, String queryString,
          List<String> schemaPath, List<String> viewPath) {
        throw new UnsupportedOperationException();
      }

      public List<RelHint> getTableHints() {
        throw new UnsupportedOperationException();
      }
    };
  }
}