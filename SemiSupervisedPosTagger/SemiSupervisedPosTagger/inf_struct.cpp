//
// Created by Mohammad Sadegh Rasooli on 1/9/15.
// Copyright (c) 2015 Mohammad Sadegh Rasooli. All rights reserved.
//

#include "inf_struct.h"

inf_struct::inf_struct(averaged_perceptron perceptron) {
	averaged_weights=perceptron.get_averaged_weights();
	tag_size=perceptron.tag_size;
	feat_size=perceptron.feat_size;
}

inf_struct::~inf_struct() {
}
