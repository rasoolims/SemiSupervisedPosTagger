//
//  main.cpp
//  SemiSupervisedPosTagger
//
//  Created by Mohammad Sadegh Rasooli on 1/6/15.
//  Copyright (c) 2015 Mohammad Sadegh Rasooli. All rights reserved.
//

#include <iostream>
#include "trainer.h"
#include "unit_test.h"

using namespace std;
int main(int argc, const char * argv[])
{
    // insert code here...
    cout << "Hello, World!\n";
    // unit_test unitTest;
    //unitTest.test_perceptron();

	//unitTest.test_utf8("/tmp/utf82.txt");
	//unitTest.test_tag_file_reader("/tmp/utf82.txt","/");

	string train_path="/Users/msr/Projects/SemiSupervisedPosTagger/SemiSupervisedPosTagger/SemiSupervisedPosTagger/sample_file/train.tag";
	string dev_path= "/Users/msr/Projects/SemiSupervisedPosTagger/SemiSupervisedPosTagger/SemiSupervisedPosTagger/sample_file/dev.tag";
	string model_path="/tmp/model";
	if(argc>3) {
		 train_path = argv[1];
		 dev_path = argv[2];
		 model_path = argv[3];
	}
	cout <<"train_path: "<<train_path<<endl;
	cout << "dev_path: "<<dev_path<<endl;
	cout << "model_path: "<<model_path<<endl;
	trainer::train(train_path,
			dev_path, model_path, 18, "_", 20);
	return 0;
}
