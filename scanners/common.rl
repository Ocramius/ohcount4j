%%{
  machine common;

  # common definitions
  spaces = [\t ]+; 
  newline = ('\r\n' | '\n' | '\f' | '\r' when { p+1 < pe && data[p+1] != '\n' });
  nonnewline = any - [\r\n\f];

  escape_seq = '\\' nonnewline;
  string_char = (nonnewline - '\\') | escape_seq;
  
  action got_newline{
    notifyNewline();
  }

  action got_spaces{
    notifyBlanks();
  }

  action start_comment{
    notifyStartComment();
  }

  action end_comment{
    notifyEndComment();
  }

  action got_code_character{
    notifyCodeCharacter();
  }

  action start_str{
    notifyStartString();
  }

  action end_str{
    notifyEndString();
  }

  action begin_define_match{
    beginDefineMatch();
  }

  action end_define_match{
    endDefineMatch();
  }

  action begin_try_match{
    beginTryMatch();
  }
  
}%%  
