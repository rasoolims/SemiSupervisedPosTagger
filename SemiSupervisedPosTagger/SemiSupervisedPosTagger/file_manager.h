//
// Created by Mohammad Sadegh Rasooli on 1/7/15.
// Copyright (c) 2015 Mohammad Sadegh Rasooli. All rights reserved.
//



#ifndef __FileManager_H_
#define __FileManager_H_

#include "vector"
#include "index_maps.h"
#include "sentence.h"
#include "fstream"
#include "sstream"

using namespace std;
class file_manager {
	public:
		static index_maps create_indexMaps(string file_path,const string delim);

		static vector<sentence> read_sentences(string file_path,const unordered_map<string,int> indexMaps,const string delim);

	private:
		static sentence get_sentence_from_line(const string line,const unordered_map<string,int> indexMaps,const string delim);

};


#endif //__FileManager_H_
