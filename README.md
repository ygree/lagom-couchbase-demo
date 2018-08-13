

Run all locally:
---------------- 

```
mvn lagom:runAll
```

hello service
-------------

Get / update users welcome messages:
```
curl -H "Content-Type: application/json" -X POST -d '{"message": "Hi"}' http://localhost:9000/api/hello/Alice
{ "done" : true }✔

curl http://localhost:9000/api/hello/Alice
Hi, Alice!✔

```


readside module
---------------

This service consumes events from a Kafka topic produced by the Hello module and persists in Couchbase

```
curl http://localhost:9000/readside-api/hello/Alice
```

CRUD example
------------

For demonstration purposes readside module also implements a direct CRUD-like access to Couchbase.

> THIS approach is NOT recommended for implementing flexible and scalable Microservices.

> ES/CQRS approach provides means to build flexible and scalable microservices promoting asynchronous
> communications between services.

```

curl -H "Content-Type: application/json" -X POST -d '{"message": "Hello"}' http://localhost:9000/crud-api/hello/Alice
{ "done" : true }✔

curl http://localhost:9000/crud-api/hello/Alice
Hello, Alice!✔

```

Couchbase
---------

[https://hub.docker.com/r/couchbase/server/]

```
docker pull couchbase/server

docker run -d --name db -p 8091-8094:8091-8094 -p 11210:11210 couchbase
```

open [http://localhost:8091]

Cassandra
---------

```
22:13 $ ~/local/apps/apache-cassandra-3.11.2/bin/cqlsh 127.0.0.1 4000
Connected to Test Cluster at 127.0.0.1:4000.
[cqlsh 5.0.1 | Cassandra 3.11.2 | CQL spec 3.4.4 | Native protocol v4]
Use HELP for help.
cqlsh>

describe keyspaces;

use <keyspace>;

describe tables;

SELECT * FROM system_schema.views;
```

Cassandra tables
----------------


tag_views  messages  tag_scanning  offsetstore  tag_write_progress  metadata snapshots


```
CREATE TABLE tag_views (
    tag_name text,
    timebucket bigint,
    timestamp timeuuid,
    persistence_id text,
    tag_pid_sequence_nr bigint,
    event blob,
    event_manifest text,
    meta blob,
    meta_ser_id int,
    meta_ser_manifest text,
    sequence_nr bigint,
    ser_id int,
    ser_manifest text,
    writer_uuid text,
    PRIMARY KEY ((tag_name, timebucket), timestamp, persistence_id, tag_pid_sequence_nr)
) WITH CLUSTERING ORDER BY (timestamp ASC, persistence_id ASC, tag_pid_sequence_nr ASC)
    AND bloom_filter_fp_chance = 0.01
    AND caching = {'keys': 'ALL', 'rows_per_partition': 'NONE'}
    AND comment = ''
    AND compaction = {'bucket_high': '1.5', 'bucket_low': '0.5', 'class': 'org.apache.cassandra.db.compaction.SizeTieredCompactionStrategy', 'enabled': 'true', 'max_threshold': '32', 'min_sstable_size': '50', 'min_threshold': '4', 'tombstone_compaction_interval': '86400', 'tombstone_threshold': '0.2', 'unchecked_tombstone_compaction': 'false'}
    AND compression = {'chunk_length_in_kb': '64', 'class': 'org.apache.cassandra.io.compress.LZ4Compressor'}
    AND crc_check_chance = 1.0
    AND dclocal_read_repair_chance = 0.1
    AND default_time_to_live = 0
    AND gc_grace_seconds = 864000
    AND max_index_interval = 2048
    AND memtable_flush_period_in_ms = 0
    AND min_index_interval = 128
    AND read_repair_chance = 0.0
    AND speculative_retry = '99PERCENTILE';


CREATE TABLE messages (
    persistence_id text,
    partition_nr bigint,
    sequence_nr bigint,
    timestamp timeuuid,
    timebucket text,
    event blob,
    event_manifest text,
    message blob,
    meta blob,
    meta_ser_id int,
    meta_ser_manifest text,
    ser_id int,
    ser_manifest text,
    tags set<text>,
    used boolean static,
    writer_uuid text,
    PRIMARY KEY ((persistence_id, partition_nr), sequence_nr, timestamp, timebucket)
) WITH CLUSTERING ORDER BY (sequence_nr ASC, timestamp ASC, timebucket ASC)
    AND bloom_filter_fp_chance = 0.01
    AND caching = {'keys': 'ALL', 'rows_per_partition': 'NONE'}
    AND comment = ''
    AND compaction = {'bucket_high': '1.5', 'bucket_low': '0.5', 'class': 'org.apache.cassandra.db.compaction.SizeTieredCompactionStrategy', 'enabled': 'true', 'max_threshold': '32', 'min_sstable_size': '50', 'min_threshold': '4', 'tombstone_compaction_interval': '86400', 'tombstone_threshold': '0.2', 'unchecked_tombstone_compaction': 'false'}
    AND compression = {'chunk_length_in_kb': '64', 'class': 'org.apache.cassandra.io.compress.LZ4Compressor'}
    AND crc_check_chance = 1.0
    AND dclocal_read_repair_chance = 0.1
    AND default_time_to_live = 0
    AND gc_grace_seconds = 864000
    AND max_index_interval = 2048
    AND memtable_flush_period_in_ms = 0
    AND min_index_interval = 128
    AND read_repair_chance = 0.0
    AND speculative_retry = '99PERCENTILE';


CREATE TABLE tag_scanning (
    persistence_id text PRIMARY KEY,
    sequence_nr bigint
) WITH bloom_filter_fp_chance = 0.01
    AND caching = {'keys': 'ALL', 'rows_per_partition': 'NONE'}
    AND comment = ''
    AND compaction = {'class': 'org.apache.cassandra.db.compaction.SizeTieredCompactionStrategy', 'max_threshold': '32', 'min_threshold': '4'}
    AND compression = {'chunk_length_in_kb': '64', 'class': 'org.apache.cassandra.io.compress.LZ4Compressor'}
    AND crc_check_chance = 1.0
    AND dclocal_read_repair_chance = 0.1
    AND default_time_to_live = 0
    AND gc_grace_seconds = 864000
    AND max_index_interval = 2048
    AND memtable_flush_period_in_ms = 0
    AND min_index_interval = 128
    AND read_repair_chance = 0.0
    AND speculative_retry = '99PERCENTILE';



CREATE TABLE offsetstore (
    eventprocessorid text,
    tag text,
    sequenceoffset bigint,
    timeuuidoffset timeuuid,
    PRIMARY KEY (eventprocessorid, tag)
) WITH CLUSTERING ORDER BY (tag ASC)
    AND bloom_filter_fp_chance = 0.01
    AND caching = {'keys': 'ALL', 'rows_per_partition': 'NONE'}
    AND comment = ''
    AND compaction = {'class': 'org.apache.cassandra.db.compaction.SizeTieredCompactionStrategy', 'max_threshold': '32', 'min_threshold': '4'}
    AND compression = {'chunk_length_in_kb': '64', 'class': 'org.apache.cassandra.io.compress.LZ4Compressor'}
    AND crc_check_chance = 1.0
    AND dclocal_read_repair_chance = 0.1
    AND default_time_to_live = 0
    AND gc_grace_seconds = 864000
    AND max_index_interval = 2048
    AND memtable_flush_period_in_ms = 0
    AND min_index_interval = 128
    AND read_repair_chance = 0.0
    AND speculative_retry = '99PERCENTILE';


CREATE TABLE tag_write_progress (
    persistence_id text,
    tag text,
    offset timeuuid,
    sequence_nr bigint,
    tag_pid_sequence_nr bigint,
    PRIMARY KEY (persistence_id, tag)
) WITH CLUSTERING ORDER BY (tag ASC)
    AND bloom_filter_fp_chance = 0.01
    AND caching = {'keys': 'ALL', 'rows_per_partition': 'NONE'}
    AND comment = ''
    AND compaction = {'class': 'org.apache.cassandra.db.compaction.SizeTieredCompactionStrategy', 'max_threshold': '32', 'min_threshold': '4'}
    AND compression = {'chunk_length_in_kb': '64', 'class': 'org.apache.cassandra.io.compress.LZ4Compressor'}
    AND crc_check_chance = 1.0
    AND dclocal_read_repair_chance = 0.1
    AND default_time_to_live = 0
    AND gc_grace_seconds = 864000
    AND max_index_interval = 2048
    AND memtable_flush_period_in_ms = 0
    AND min_index_interval = 128
    AND read_repair_chance = 0.0
    AND speculative_retry = '99PERCENTILE';



CREATE TABLE metadata (
    persistence_id text PRIMARY KEY,
    deleted_to bigint,
    properties map<text, text>
) WITH bloom_filter_fp_chance = 0.01
    AND caching = {'keys': 'ALL', 'rows_per_partition': 'NONE'}
    AND comment = ''
    AND compaction = {'class': 'org.apache.cassandra.db.compaction.SizeTieredCompactionStrategy', 'max_threshold': '32', 'min_threshold': '4'}
    AND compression = {'chunk_length_in_kb': '64', 'class': 'org.apache.cassandra.io.compress.LZ4Compressor'}
    AND crc_check_chance = 1.0
    AND dclocal_read_repair_chance = 0.1
    AND default_time_to_live = 0
    AND gc_grace_seconds = 864000
    AND max_index_interval = 2048
    AND memtable_flush_period_in_ms = 0
    AND min_index_interval = 128
    AND read_repair_chance = 0.0
    AND speculative_retry = '99PERCENTILE';


CREATE TABLE hello.snapshots (
    persistence_id text,
    sequence_nr bigint,
    meta blob,
    meta_ser_id int,
    meta_ser_manifest text,
    ser_id int,
    ser_manifest text,
    snapshot blob,
    snapshot_data blob,
    timestamp bigint,
    PRIMARY KEY (persistence_id, sequence_nr)
) WITH CLUSTERING ORDER BY (sequence_nr DESC)
    AND bloom_filter_fp_chance = 0.01
    AND caching = {'keys': 'ALL', 'rows_per_partition': 'NONE'}
    AND comment = ''
    AND compaction = {'bucket_high': '1.5', 'bucket_low': '0.5', 'class': 'org.apache.cassandra.db.compaction.SizeTieredCompactionStrategy', 'enabled': 'true', 'max_threshold': '32', 'min_sstable_size': '50', 'min_threshold': '4', 'tombstone_compaction_interval': '86400', 'tombstone_threshold': '0.2', 'unchecked_tombstone_compaction': 'false'}
    AND compression = {'chunk_length_in_kb': '64', 'class': 'org.apache.cassandra.io.compress.LZ4Compressor'}
    AND crc_check_chance = 1.0
    AND dclocal_read_repair_chance = 0.1
    AND default_time_to_live = 0
    AND gc_grace_seconds = 864000
    AND max_index_interval = 2048
    AND memtable_flush_period_in_ms = 0
    AND min_index_interval = 128
    AND read_repair_chance = 0.0
    AND speculative_retry = '99PERCENTILE';

```


