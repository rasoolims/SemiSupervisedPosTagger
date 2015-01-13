//
// Created by Mohammad Sadegh Rasooli on 1/8/15.
// Copyright (c) 2015 Mohammad Sadegh Rasooli. All rights reserved.
//


#ifndef __trainer_H_
#define __trainer_H_

#include "string"
#include "index_maps.h"
#include "averaged_perceptron.h"
#include "inf_struct.h"


using namespace std;
class trainer {
public:
	static void train(const string train_file_path, const string dev_file_path, const string model_path, const int feat_size,const string delim, const int max_iterations);

	void save_model(const string model_path,const index_maps maps, const inf_struct info);
};


#endif //__trainer_H_
