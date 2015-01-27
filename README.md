Pos Tagger
=======================


* Train a tagger:

	* java -jar SemiSupervisedTagger.jar train -input [input-file] -model [model-file]
		* Other Options:
     
				* -dev [dev-file]  dev file address
				* -viterbi   if you want to use Viterbi decoding (default: beam decoding)
				* -update:[mode]  for beam training; three #modes: max_viol, early, standard (default: max_viol)
				* -delim [delim]   put delimiter string in [delim] for word tag separator (default _) e.g. -delim /
				* beam:[#b]  put a number [#b] for beam size (default:5); e.g. beam:10
				* iter:[#i]  put a number [#i] for training iterations (default:20); e.g. iter:10

				* NOTE: in every iteration the model file for that iteration will have the format [model-file].iter_#iter e.g. model.iter_3

* Tag a file:
	* java -jar SemiSupervisedTagger.jar tag -input [input-file] -model [model-file] -output [output-file]
		* Other Options:
     	* -delim [delim]   put delimiter string in [delim] for word tag separator (default _) e.g. -delim /


* Tag a partially tagged file:
	* java -jar SemiSupervisedTagger.jar partial_tag -input [input-file] -model [model-file] -output [output-file]
		* For words with no tag information, put *** as the tag; e.g. After\_IN our\_\*\*\* discussion\_\*\*\* ._.
		* Other Options:
     		* -delim [delim]   put delimiter string in [delim] for word tag separator (default _) e.g. -delim /



