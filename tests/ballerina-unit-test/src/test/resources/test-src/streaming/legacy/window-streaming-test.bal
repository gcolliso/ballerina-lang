// Copyright (c) 2018 WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
//
// WSO2 Inc. licenses this file to you under the Apache License,
// Version 2.0 (the "License"); you may not use this file except
// in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

import ballerina/runtime;

type StatusCount record {
    string status;
    int totalCount;
};

type Teacher record {
    string name;
    int age;
    string status;
    string batch;
    string school;
};

StatusCount[] globalStatusCountArray = [];
int index = 0;

stream<StatusCount> statusCountStream1 = new;
stream<Teacher> teacherStream5 = new;

function testWindowQuery() {

    forever {
        from teacherStream5 where age > 18 window lengthBatch(3)
        select status, count(status) as totalCount
        group by status
        => (StatusCount[] emp) {
            foreach var e in emp {
                statusCountStream1.publish(e);
            }
        }
    }
}

function startWindowQuery() returns (StatusCount[]) {

    testWindowQuery();

    Teacher t1 = {name:"Raja", age:25, status:"single", batch:"LK2014", school:"Hindu College"};
    Teacher t2 = {name:"Shareek", age:33, status:"single", batch:"LK1998", school:"Thomas College"};
    Teacher t3 = {name:"Nimal", age:45, status:"married", batch:"LK1988", school:"Ananda College"};

    statusCountStream1.subscribe(printStatusCount);

    teacherStream5.publish(t1);
    teacherStream5.publish(t2);
    teacherStream5.publish(t3);

    runtime:sleep(1000);
    return globalStatusCountArray;
}

function printStatusCount(StatusCount s) {
    addToGlobalStatusCountArray(s);
}

function addToGlobalStatusCountArray(StatusCount s) {
    globalStatusCountArray[index] = s;
    index = index + 1;
}
