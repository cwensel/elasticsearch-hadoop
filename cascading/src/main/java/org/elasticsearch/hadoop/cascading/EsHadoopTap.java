/*
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.elasticsearch.hadoop.cascading;

import cascading.flow.Flow;
import cascading.flow.FlowProcess;
import cascading.tap.SinkMode;
import cascading.tap.Tap;
import cascading.tap.hadoop.io.HadoopTupleEntrySchemeCollector;
import cascading.tap.hadoop.io.HadoopTupleEntrySchemeIterator;
import cascading.tuple.Fields;
import cascading.tuple.TupleEntryCollector;
import cascading.tuple.TupleEntryIterator;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.RecordReader;

import java.io.IOException;
import java.util.Properties;

/**
 * Hadoop-based Cascading Tap.
 */
@SuppressWarnings("rawtypes")
class EsHadoopTap extends Tap<Configuration, RecordReader, OutputCollector> {

    private static final long serialVersionUID = 7910041489511719399L;

    private final String target;

    public EsHadoopTap(String host, int port, String index, String query, Fields fields, Properties props) {
        super(new EsHadoopScheme(host, port, index, query, fields, props), SinkMode.UPDATE);
        this.target = index;
    }

    @Override
    public String getIdentifier() {
        return target;
    }

    @Override
    public void flowConfInit(Flow<Configuration> flow) {
        CascadingUtils.addSerializationToken(flow.getConfig());
    }

    @Override
    public TupleEntryIterator openForRead(FlowProcess<? extends Configuration> flowProcess, RecordReader input) throws IOException {
        return new HadoopTupleEntrySchemeIterator(flowProcess, this, input);
    }

    @Override
    public TupleEntryCollector openForWrite(FlowProcess<? extends Configuration> flowProcess, OutputCollector output) throws IOException {
        return new HadoopTupleEntrySchemeCollector(flowProcess, this, output);
    }

    @Override
    public boolean createResource(Configuration conf) throws IOException {
        return false;
    }

    @Override
    public boolean deleteResource(Configuration conf) throws IOException {
        return false;
    }

    @Override
    public boolean resourceExists(Configuration conf) throws IOException {
        return true;
    }

    @Override
    public long getModifiedTime(Configuration conf) throws IOException {
        return 0;
    }
}