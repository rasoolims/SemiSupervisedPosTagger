//
// Created by Mohammad Sadegh Rasooli on 1/8/15.
// Copyright (c) 2015 Mohammad Sadegh Rasooli. All rights reserved.
//


#ifndef __trainer_H_
#define __trainer_H_

#include "string"
#include "index_maps.h"
#include "averaged_perceptron.h"


using namespace std;
class trainer {
public:
	static void train(string train_file_path,string dev_file_path,string model_path,int feat_size,const string delim,int max_iterations);

	void save_model(string model_path,index_maps maps, averaged_perceptron perceptron);
};


#endif //__trainer_H_
