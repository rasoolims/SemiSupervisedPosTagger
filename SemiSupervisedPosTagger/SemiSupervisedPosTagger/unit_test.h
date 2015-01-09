//
// Created by Mohammad Sadegh Rasooli on 1/6/15.
// Copyright (c) 2015 Mohammad Sadegh Rasooli. All rights reserved.
//


#ifndef __UnitTest_H_
#define __UnitTest_H_

using namespace std;
class unit_test {
public:
    unit_test();
    void test_perceptron();
	void test_utf8(string file_path);
	void test_tag_file_reader(string file_path,const string delim);
};


#endif //__UnitTest_H_
