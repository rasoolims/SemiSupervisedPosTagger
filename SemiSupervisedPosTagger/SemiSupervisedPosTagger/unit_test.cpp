//
// Created by Mohammad Sadegh Rasooli on 1/6/15.
// Copyright (c) 2015 Mohammad Sadegh Rasooli. All rights reserved.
//

#include <iostream>
#include "unit_test.h"
#include "averaged_perceptron.h"
#include "fstream"
#include "sstream"
#include "file_manager.h"

using namespace std;
void unit_test::test_perceptron() {
    averaged_perceptron perceptron(10,10);
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

    cout<<"checking done!"<<endl;
}

unit_test::unit_test() {
}

void unit_test::test_utf8(string file_path) {
	string a=u8"خدانگهدار" ;
	string b=u8"hello";
	wstring ab1(L"hi");
	wstring ab2(L"سلام");
	cout << b.length()/sizeof(char)<<" "<< a.length()/sizeof(char)<<" "<<ab1.length() <<" "<<ab2.length() <<endl;

	ifstream reader(file_path);
   	while (!reader.eof()){
	   string read;
	    getline(reader, read);
	    istringstream iss(read);
	    do
	    {
		    string sub;
		    iss >> sub;
		    if (sub.length()>0) {
			    string word=sub.substr(0, sub.rfind("/"));
			    string tag=sub.substr(sub.rfind("/")+1);
			    cout << "Substring: " << tag << " "<< word << endl;
		    }
	    } while (iss);

	    cout << read << endl;
	    cout << read.length() << endl;
    }

}

void unit_test::test_tag_file_reader(string file_path,const string delim) {
	cout << "create index maps"<<endl;
	index_maps indexMaps= file_manager::create_indexMaps(file_path,delim);
	cout << "create sentences"<<endl;
	vector<sentence> sentences=file_manager::read_sentences(file_path, indexMaps.string_dic,delim);

	sentence sen=sentences.at(1);
	vector<int> features1=	sen.get_features(2, 4, 5);
	vector<int> features2=	sen.get_features(-1, 4, 5);
	vector<int> features3=	sen.get_features(0, 4, 5);
	vector<int> features4=	sen.get_features(5, 4, 5);
	vector<int> features5=	sen.get_features(3, 4, 5);
}
