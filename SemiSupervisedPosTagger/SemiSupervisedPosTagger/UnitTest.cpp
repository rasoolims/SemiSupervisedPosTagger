//
// Created by Mohammad Sadegh Rasooli on 1/6/15.
// Copyright (c) 2015 Mohammad Sadegh Rasooli. All rights reserved.
//

#include <iostream>
#include "fstream"
#include "UnitTest.h"
#include "AveragedPerceptron.h"

using namespace std;
void UnitTest::test_perceptron() {
    AveragedPerceptron perceptron(10,10);
    perceptron.change_weight(3, 2, 87, 1);
    perceptron.increment_iteration();
    perceptron.change_weight(1, 9, 87, 2);
    perceptron.increment_iteration();
    perceptron.change_weight(0, 9, 87, 2);
    perceptron.increment_iteration();
    perceptron.change_weight(3, 2, 87, -3);
    perceptron.increment_iteration();
    perceptron.change_weight(1, 8, 342, -1);
    perceptron.increment_iteration();
    cout<< perceptron.size()<<endl;

    long features[10];
    features[2]=87;

    int arr_size= sizeof(features)/ sizeof(long);

    cout<<perceptron.score(features,arr_size, 3, true)<<endl;
    cout<<perceptron.score(features,arr_size, 3, false)<<endl;
    perceptron.save_model("/tmp/saved");

    AveragedPerceptron* perceptron1=AveragedPerceptron::load_model("/tmp/saved");
    cout<< perceptron1->size()<<endl;

    cout<<"checking done!"<<endl;

}

UnitTest::UnitTest() {
}
