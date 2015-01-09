//
// Created by Mohammad Sadegh Rasooli on 1/7/15.
// Copyright (c) 2015 Mohammad Sadegh Rasooli. All rights reserved.
//

#include "index_maps.h"

index_maps::index_maps(unordered_map<string, int> string_dic, vector<string> reverse_dict,int tag_size) {
  this->string_dic=string_dic;
  this->reverse_dict=reverse_dict;
  this->tag_size=tag_size;
}

index_maps::~index_maps() {

}
