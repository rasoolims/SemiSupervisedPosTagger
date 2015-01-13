//
// Created by Mohammad Sadegh Rasooli on 1/9/15.
// Copyright (c) 2015 Mohammad Sadegh Rasooli. All rights reserved.
//


#include "index_maps.h"
#include "averaged_perceptron.h"

#ifndef __inf_struct_H_
#define __inf_struct_H_


class inf_struct {
public:
	unordered_map<int, float>** averaged_weights;
	int tag_size;
	int feat_size;

	inf_struct(averaged_perceptron perceptron);

	~inf_struct();
};


#endif //__inf_struct_H_
