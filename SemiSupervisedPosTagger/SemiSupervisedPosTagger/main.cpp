//
//  main.cpp
//  SemiSupervisedPosTagger
//
//  Created by Mohammad Sadegh Rasooli on 1/6/15.
//  Copyright (c) 2015 Mohammad Sadegh Rasooli. All rights reserved.
//

#include <iostream>
#include "averaged_perceptron.h"
#include "unit_test.h"

using namespace std;
int main(int argc, const char * argv[])
{
    // insert code here...
    cout << "Hello, World!\n";
    unit_test unitTest;
    //unitTest.test_perceptron();

	//unitTest.test_utf8("/tmp/utf82.txt");
	unitTest.test_tag_file_reader("/tmp/utf82.txt");

	return 0;
}
