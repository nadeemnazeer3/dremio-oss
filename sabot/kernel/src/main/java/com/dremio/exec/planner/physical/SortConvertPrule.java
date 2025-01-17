/*
 * Copyright (C) 2017-2018 Dremio Corporation
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
package com.dremio.exec.planner.physical;

import org.apache.calcite.plan.Convention;
import org.apache.calcite.plan.RelOptRule;
import org.apache.calcite.plan.RelOptRuleCall;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.convert.ConverterRule;
import org.apache.calcite.rel.core.Sort;

/**
 * Rule that converts an {@link Sort} to a physical {@link SortPrel}, implemented by a Dremio "order" operation.
 *
 * The {@link Sort} is added in optiq's AbstractConvert call, when it enforces certain "order" to the input stream.
 * Dremio uses this rule to convert such sort enforcer into physical {@link SortPrel}.
 */
public class SortConvertPrule extends ConverterRule {
  public static final RelOptRule INSTANCE = new SortConvertPrule("SortConvertPrule", Convention.NONE);
  //public static final RelOptRule INSTANCE_SRC_LOGICAL = new SortPrule("SortPrule:Src_Logical", Rel.LOGICAL);

  private SortConvertPrule(String description, Convention srcConvention) {
    super(Sort.class, srcConvention, Prel.PHYSICAL, description);
  }

  @Override
  public boolean matches(RelOptRuleCall call) {
    final Sort sort = call.rel(0);
    return sort.offset == null && sort.fetch == null;
  }

  @Override
  public RelNode convert(RelNode r) {
    Sort rel = (Sort) r;
    return SortPrel.create(rel.getCluster(),
                           rel.getInput().getTraitSet().replace(Prel.PHYSICAL).plus(rel.getCollation()),
                           convert(rel.getInput(), rel.getInput().getTraitSet().replace(Prel.PHYSICAL).simplify()),
                           rel.getCollation());
  }
}
