
#ifndef NEGATIVE_GRAPH_CYCLE_HPP_
#define NEGATIVE_GRAPH_CYCLE_HPP_

#include <exception>

class NegativeGraphCycle : public std::exception {
public:
  const char* what() const throw();
};

// const char* NegativeGraphCycle::what() const throw() {
//   return "Negative Graph Cycle!";
// }

#endif
