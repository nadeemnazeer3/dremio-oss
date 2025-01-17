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
package com.dremio.exec.physical.impl.writer;

import static com.dremio.exec.store.parquet.ParquetFormatDatasetAccessor.PARQUET_TEST_SCHEMA_FALLBACK_ONLY_VALIDATOR;

import org.junit.After;
import org.junit.Before;

public class TestParquetWriterSchemaFallback extends TestParquetWriter {

  private AutoCloseable ac;
  @Before
  public void setUp() throws Exception {
    ac = withSystemOption(PARQUET_TEST_SCHEMA_FALLBACK_ONLY_VALIDATOR, true);
  }

  @After
  public void tearDown() throws Exception {
    ac.close();
  }
}
