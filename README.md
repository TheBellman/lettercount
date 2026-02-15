# lettercount
Simple demonstration of divide-and-conquer to count letters in an arbitrarily large file.

There are a bunch of caveats around this, beginning with the obvious that this is not intended for anything remotely like production use. Also the counts per-character are integers, so "arbitrarily large" means "with less than about  111,669,149,640 letters". The input file is assumed to have UTF-8 encoding, and only alphabetic characters are counted.

## Usage
```
usage: LetterCount
 -?,--help            print this message
 -f,--file <arg>      specify the input file
 -n,--threads <arg>   optionally specify the number of worker threads
                      (defaults to 4)
 -v,--version         print version
 ```
 
## Performance notes
The background for this was an off-hand question in a conversation about how to structure code which spins out some worker threads and then gathers the results. This got me thinking about how to present that cleanly, and with pretty good performance characteristics. Initially I was going to devolve reading segments of the file to the worker threads, but some consideration showed that this was a daft idea: on a single hard disk or file system, the behaviour of (in essence) skipping the read head all around the file has highly unpredictable performance characterisics. On my laptop it proved slower to segment the file than it was to have a single thread that read it from top top bottom.

In reality if we are doing a complex process across a large file the best way to do it is to segment the file physically across a bunch of storage nodes, which means that the better solution now would be to use something like Hadoop to do the map-reduce for us, reducing our processing code to a much simpler piece of work.

There are some small nods towards performance and thread safety, although the classes in place are categorically not thread safe:

1. the `Counter` attempts to ensure that the `report()` operation returns consistent results for the state of processing reached at the time we call it - the internal accumulator has a mutex on them so that it can either be read or written at a given point in time;
2. the `CounterWorker` uses a `MutableInt` to accumulate counts in order to reduce the number of operations against the internal `Map`
3. using a `TreeMap` for the accumulator gives us sorting for free.

## License

Copyright 2026 Little Dog Digital

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.

You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
