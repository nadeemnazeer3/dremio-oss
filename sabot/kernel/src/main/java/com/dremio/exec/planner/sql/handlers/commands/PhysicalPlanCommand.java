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
package com.dremio.exec.planner.sql.handlers.commands;

import com.dremio.exec.ops.QueryContext;
import com.dremio.exec.physical.PhysicalPlan;
import com.dremio.exec.planner.PhysicalPlanReader;
import com.dremio.exec.planner.observer.AttemptObserver;
import com.dremio.exec.proto.CoordExecRPC.FragmentCodec;
import com.dremio.exec.work.rpc.CoordToExecTunnelCreator;
import com.dremio.resource.ResourceAllocator;
import com.google.protobuf.ByteString;

// should be deprecated once tests are removed.
public class PhysicalPlanCommand extends AsyncCommand {

  private final PhysicalPlanReader reader;
  private final ByteString plan;

  private PhysicalPlan physicalPlan;

  public PhysicalPlanCommand(
    CoordToExecTunnelCreator tunnelCreator,
    QueryContext context,
    PhysicalPlanReader reader,
    AttemptObserver observer,
    ByteString plan,
    ResourceAllocator queryResourceManager) {
    super(context, queryResourceManager, observer, reader, tunnelCreator);
    this.reader = reader;
    this.observer = observer;
    this.plan = plan;
  }

  @Override
  public double plan() throws Exception {
    physicalPlan = reader.readPhysicalPlan(this.plan, FragmentCodec.NONE);
    return physicalPlan.getCost();
  }

  @Override
  protected PhysicalPlan getPhysicalPlan() {
    return physicalPlan;
  }

  @Override
  public CommandType getCommandType() {
    return CommandType.ASYNC_QUERY;
  }

  @Override
  public String getDescription() {
    return "execute; query";
  }
}
